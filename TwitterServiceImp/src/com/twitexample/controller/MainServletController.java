package com.twitexample.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

@WebServlet("/MainServlet")
public class MainServletController extends HttpServlet {
	private OAuthService oAuthservice = null;
	private Token accessTokenValue = null;
	private Token requestTokenValue = null;
	private String twitterStatus = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String router = (String)request.getParameterMap().keySet().toArray()[0];
		
		if(!router.equalsIgnoreCase("login")){
			router = (String)request.getParameterMap().keySet().toArray()[2];
			System.out.println("router : "+ router);
		}
		
		switch (router) {
        case "login":
        	oAuthservice = new ServiceBuilder().provider(TwitterApi.class).apiKey("oFu39jwhsjO6TR9Vg6uQ9FgTA")
			.apiSecret("2KzwKiTzxY13UK9FRSJSkH3GSGuljJjxBj6Y91H53PtVLcNnuJ")
			.callback("http://127.0.0.1:8080/TwitterServiceImp/showAPIButtons.jsp").build();		    
			requestTokenValue = oAuthservice.getRequestToken();
			String authorizationlink = oAuthservice.getAuthorizationUrl(requestTokenValue);
			response.sendRedirect(response.encodeRedirectURL(authorizationlink));
            break;
        case "reTweet":
        	storeAccessTokenValue(request);
        	HttpSession session = request.getSession();
        	session.setAttribute("service", oAuthservice);
			session.setAttribute("accessToken", accessTokenValue);
			request.getRequestDispatcher("/ReTweet").forward(request, response);
            break;
        case "getFollowers":
        	storeAccessTokenValue(request);
        	session = request.getSession();
        	session.setAttribute("service", oAuthservice);
			session.setAttribute("accessToken", accessTokenValue);
			request.getRequestDispatcher("/FindFollowers").forward(request, response);
            break;
        case "timelineTweets":
        	storeAccessTokenValue(request);
        	session = request.getSession();
        	session.setAttribute("service", oAuthservice);
			session.setAttribute("accessToken", accessTokenValue);
			request.getRequestDispatcher("/TimelineTweet").forward(request, response);
            break;
        case "searchTweets":
        	storeAccessTokenValue(request);
        	session = request.getSession();
        	session.setAttribute("service", oAuthservice);
			session.setAttribute("accessToken", accessTokenValue);
			request.getRequestDispatcher("/SearchTweets").forward(request, response);
            break;
        case "mentionTimeline":
        	storeAccessTokenValue(request);
        	session = request.getSession();
        	session.setAttribute("service", oAuthservice);
			session.setAttribute("accessToken", accessTokenValue);
			request.getRequestDispatcher("/MentionTimeline").forward(request, response);
        	break;
        case "verifyCredentials":
        	storeAccessTokenValue(request);
        	session = request.getSession();
        	session.setAttribute("service", oAuthservice);
			session.setAttribute("accessToken", accessTokenValue);
			request.getRequestDispatcher("/AccountInfo").forward(request, response);
        	break;	
        default:
        	storeAccessTokenValue(request);
        	session = request.getSession();
        	session.setAttribute("service", oAuthservice);
			session.setAttribute("accessToken", accessTokenValue);
			request.getRequestDispatcher("/FindFavouriteTweets").forward(request, response);
            break;
    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameter("tweet") != null) {
			storeAccessTokenValue(request);
			request.getRequestDispatcher("/postTweet.jsp").forward(request, response);
		} else if (request.getParameter("postStatus") != null && request.getParameter("status") != null) {
			storeAccessTokenValue(request);
			twitterStatus = request.getParameter("status");
			String requestUrl = "https://api.twitter.com/1.1/statuses/update.json";
			invokeTwitterAPI(Verb.POST, requestUrl);
			request.setAttribute("status", twitterStatus);
			request.getRequestDispatcher("/postTweet.jsp").forward(request, response);
		}
	}
	
	protected String invokeTwitterAPI(Verb httpMethod, String url) {
		String responseBody = "";
		try {
			OAuthRequest request = new OAuthRequest(httpMethod, url);
			if (twitterStatus != null) {
				request.addBodyParameter("status", twitterStatus);
			}
			oAuthservice.signRequest(accessTokenValue, request);
			Response response = request.send();
			System.out.println(response.getBody());
			responseBody = response.getBody();

		} catch (Exception e) {
			System.out.println("Exception while serving twitter request" + e.getMessage());
		}
		return responseBody;

	}

	public void storeAccessTokenValue(HttpServletRequest request) {
		Verifier oAuthverifier = null;
		if (request.getParameter("oauth_verifier") != null) {
			if (oAuthverifier == null) {
				oAuthverifier = new Verifier(request.getParameter("oauth_verifier"));
			}
			if (accessTokenValue == null) {
				accessTokenValue = oAuthservice.getAccessToken(requestTokenValue, oAuthverifier);
			}
		}
	}

}
