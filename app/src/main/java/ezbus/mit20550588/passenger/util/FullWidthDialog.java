package ezbus.mit20550588.passenger.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

public class FullWidthDialog extends Dialog {

    public FullWidthDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the width of the screen
        WindowManager wm = getWindow().getWindowManager();
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        int screenWidth = size.x;

        // Set the width of the dialog to match the screen width
        getWindow().setLayout(screenWidth, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
