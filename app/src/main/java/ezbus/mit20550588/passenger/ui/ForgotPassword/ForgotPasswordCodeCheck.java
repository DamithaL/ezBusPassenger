package ezbus.mit20550588.passenger.ui.ForgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ezbus.mit20550588.passenger.R;

public class ForgotPasswordCodeCheck extends AppCompatActivity {

    // Define your EditText fields
    EditText editTextDigit1, editTextDigit2, editTextDigit3, editTextDigit4, editTextDigit5, editTextDigit6;

    // Array to keep track of the highlight state of each EditText
    private boolean[] isHighlighted = new boolean[6];

    // Variable to store the last known text in the EditText field
    private CharSequence lastText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_code_check);

        // EZBus Passenger app main text

        TextView textView0 = findViewById(R.id.main_app_name);
        String html = "<font color=#025a66>EZBus</font> <font color=#0A969F>Passenger</font>";
        textView0.setText(Html.fromHtml(html));


        // Resend Text

        TextView textView = findViewById(R.id.ResendText);

        // Create the SpannableString
        SpannableString spannableString = new SpannableString("If you still haven't received the code, Resend code");

        // Set a ClickableSpan for the "Resend code" part
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Handle the click event for "Resend cpde"
                // Update the text to "Please wait until you receive a code"
                textView.setText("Please wait until you receive a code");
            }
        };

        // Set the ClickableSpan for the specific range
        spannableString.setSpan(clickableSpan, 40, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Resend code" part bold
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 40, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Resend code" part a different color
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.colorPrimary)), 40, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        textView.setText(spannableString);

        // Make the TextView clickable
        textView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());



        // verify button
        Button VerifyButton = (Button) this.findViewById(R.id.verifyButton);
        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        CreateNewPassword.class);
                startActivity(intent);
            }
        });


        // Initialize your EditText fields
        editTextDigit1 = findViewById(R.id.editTextDigit1);
        editTextDigit2 = findViewById(R.id.editTextDigit2);
        editTextDigit3 = findViewById(R.id.editTextDigit3);
        editTextDigit4 = findViewById(R.id.editTextDigit4);
        editTextDigit5 = findViewById(R.id.editTextDigit5);
        editTextDigit6 = findViewById(R.id.editTextDigit6);

        // Set the focus change listener for each EditText
        setFocusChangeListener(editTextDigit1,0);
        setFocusChangeListener(editTextDigit2,1);
        setFocusChangeListener(editTextDigit3,2);
        setFocusChangeListener(editTextDigit4,3);
        setFocusChangeListener(editTextDigit5,4);
        setFocusChangeListener(editTextDigit6,5);

        // Set the initial digit focused
        editTextDigit1.requestFocus();

        // Set a TextWatcher for each EditText
        addTextWatcher(editTextDigit1, 0);
        addTextWatcher(editTextDigit2, 1);
        addTextWatcher(editTextDigit3, 2);
        addTextWatcher(editTextDigit4, 3);
        addTextWatcher(editTextDigit5, 4);
        addTextWatcher(editTextDigit6, 5);

    }


    private void setFocusChangeListener(final EditText editText,  final int index) {
        // Set the focus change listener for the EditText
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Check if the EditText has focus
                if (hasFocus) {

                    if (!isHighlighted[index]) {
                        // Set the background to the focused drawable
                        editText.setBackgroundResource(R.drawable.rounded_edit_digit_focused_bg);

                    }

                } else {

                    //if already completed
                    if (isHighlighted[index]) {
                    } else {

                        // otherwise Set the background to the normal
                        editText.setBackgroundResource(R.drawable.rounded_edittext_bg);
                    }
                }
            }
        });
    }


    private void addTextWatcher(final EditText editText, final int index) {
        editText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                lastText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if a number is entered
                if (count > 0) {
                    // Set the background to highlighted if not already highlighted
                    if (!isHighlighted[index]) {
                        editText.setBackgroundResource(R.drawable.highlighted_digitbox);
                        isHighlighted[index] = true;
                    }

                    // Move focus to the next EditText field
                    EditText nextEditText = findNextEditText(editText);
                    if (nextEditText != null) {
                        nextEditText.requestFocus();
                    }

                } else {
                    // Set the background to normal if not already normal
                    if (isHighlighted[index]) {
                        editText.setBackgroundResource(R.drawable.rounded_edittext_bg);
                        isHighlighted[index] = false;
                    }

                    // No number entered, delete the previous number
                    deletePreviousNumber(editText, lastText);



                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Check if the backspace key is pressed
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // Handle backspace key press
                    // Get the current text of the EditText
                    String currentText = editText.getText().toString();
                    // Check if there is text to delete
                    if (!currentText.isEmpty()) {
                        // Delete the last character
                        editText.setText(currentText.substring(0, currentText.length() - 1));
                    }
                    if (currentText.isEmpty()) {
                        // Move focus to the previous EditText field
                        moveFocusToPrevious(index);
                    }


                    return true; // Indicate that the event has been handled
                }
                return false;
            }
        });
    }




    // Find the next EditText programmatically
    private EditText findNextEditText(EditText currentEditText) {
        switch (currentEditText.getId()) {
            case R.id.editTextDigit1:
                return findViewById(R.id.editTextDigit2);
            case R.id.editTextDigit2:
                return findViewById(R.id.editTextDigit3);
            case R.id.editTextDigit3:
                return findViewById(R.id.editTextDigit4);
            case R.id.editTextDigit4:
                return findViewById(R.id.editTextDigit5);
            case R.id.editTextDigit5:
                return findViewById(R.id.editTextDigit6);
            case R.id.editTextDigit6:
                // The last EditText, return null
                return null;
            default:
                return null;
        }
    }





    // Find the previous EditText programmatically
    private EditText findPreviousEditText(int currentIndex) {
        switch (currentIndex) {
            case 0:
                return null; // No previous EditText for the first one
            case 1:
                return findViewById(R.id.editTextDigit1);
            case 2:
                return findViewById(R.id.editTextDigit2);
            case 3:
                return findViewById(R.id.editTextDigit3);
            case 4:
                return findViewById(R.id.editTextDigit4);
            case 5:
                return findViewById(R.id.editTextDigit5);
            default:
                return null;
        }
    }

    // Move focus to the previous EditText programmatically
    private void moveFocusToPrevious(int currentIndex) {
        EditText previousEditText = findPreviousEditText(currentIndex);
        if (previousEditText != null) {
            previousEditText.requestFocus();
        }
    }

    // Delete the previous number in the EditText programmatically
    private void deletePreviousNumber(EditText editText, CharSequence beforeText) {
        if (beforeText.length() > 0) {
            // Only delete the previous number if there was one
            editText.setText(beforeText.subSequence(0, beforeText.length() - 1));
        }
    }


}