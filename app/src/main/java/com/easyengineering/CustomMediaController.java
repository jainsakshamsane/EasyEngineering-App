package com.easyengineering;
import android.content.Context;
import android.widget.MediaController;

public class CustomMediaController extends MediaController {

    public CustomMediaController(Context context) {
        super(context);
    }

    // Override the hide method to prevent hiding the controls
    @Override
    public void hide() {
        // Do nothing to prevent hiding
    }
}
