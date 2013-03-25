package com.cattong.sns.impl.facebook;

import com.cattong.commons.http.auth.Authorization;
import com.cattong.commons.oauth.config.OAuthConfigBase;

public class FacebookOAuthConfig extends OAuthConfigBase {
	private static final long serialVersionUID = -1979900827947224419L;

	public FacebookOAuthConfig() {
    	this.setAuthVersion(Authorization.AUTH_VERSION_OAUTH_2);
    	
        this.setConsumerKey("301548906522958");
        this.setConsumerSecret("d6791ea182ab25487a07c326f3e7189b");
        this.setCallbackUrl("http://www.yibo.me/authorize/getAccessToken.do");
        this.setAccessTokenUrl("https://graph.facebook.com/oauth/access_token");
        this.setAuthorizeUrl("https://graph.facebook.com/oauth/authorize");

        // https://developers.facebook.com/docs/reference/api/permissions/
        String scope = 
            "offline_access," + // 离线访问，获得长时间有效的Token

            // User and friends Permissions
            "user_about_me," +
            "friends_about_me,"+
            "user_activities,"+
            "friends_activities," +
            "user_birthday," +
            "friends_birthday," +
            "user_checkins," +
            "friends_checkins," +
            "user_education_history," +
            "friends_education_history," +
            "user_events," +
            "friends_events," +
            "user_groups," +
            "friends_groups," +
            "user_hometown," +
            "friends_hometown," +
            "user_interests," +
            "friends_interests," +
            "user_likes," +
            "friends_likes," +
            "user_location," +
            "friends_location," +
            "user_notes," +
            "friends_notes," +
            "user_online_presence," +
            "friends_online_presence," +
            "user_photo_video_tags," +
            "friends_photo_video_tags," +
            "user_photos," +
            "friends_photos," +
            "user_relationships," +
            "friends_relationships," +
            "user_relationship_details," +
            "friends_relationship_details," +
            "user_religion_politics," +
            "friends_religion_politics," +
            "user_status," +
            "friends_status," +
            "user_videos," +
            "friends_videos," +
            "user_website," +
            "friends_website," +
            "user_work_history," +
            "friends_work_history," +

            // Extended Permissions
            "read_friendlists," +
            "read_insights," +
            "read_mailbox," +
            "read_requests," +
            "read_stream," +
            "xmpp_login," +
            "ads_management," +
            "create_event," +
            "manage_friendlists," +
            "manage_notifications," +
            "publish_checkins," +
            "publish_stream," +
            "rsvp_event," +
            "sms," +
            "publish_actions";
        
        

        this.setOAuthScope(scope);
    }

}
