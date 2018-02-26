
package com.netas.utilities;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities
{
	public static String          SENDER_ID              = "43226793295";
	public static String          DISPLAY_MESSAGE_ACTION = "com.netas.DISPLAY_MESSAGE";
	public static final String    EXTRA_MESSAGE          = "message";
	public static final String    EXTRA_PARAM            = "param";
	
	public static void displayMessage( Context context , String message , String param )
	{
		Intent intent = new Intent( DISPLAY_MESSAGE_ACTION );
		intent.putExtra( EXTRA_MESSAGE , message );
		intent.putExtra( EXTRA_PARAM , param );
		context.sendBroadcast( intent );
	}
}

