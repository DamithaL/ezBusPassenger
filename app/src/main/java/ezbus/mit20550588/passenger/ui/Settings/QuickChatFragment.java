package ezbus.mit20550588.passenger.ui.Settings;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import ezbus.mit20550588.passenger.R;

public class QuickChatFragment extends Fragment {

    public QuickChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quick_chat, container, false);

        // Access LinearLayout from the activity's layout
        LinearLayout ContactText = getActivity().findViewById(R.id.ContactText);
        LinearLayout MainButtonGroup = getActivity().findViewById(R.id.MainButtonGroup);
        ImageView contact_us_header_bg = getActivity().findViewById(R.id.contact_us_header_bg);

        // Set the visibility to GONE when the fragment loads
        if (ContactText != null) {
            ContactText.setVisibility(View.GONE);
        }

        // Set the visibility to GONE when the fragment loads
        if (MainButtonGroup != null) {
            MainButtonGroup.setVisibility(View.GONE);
        }

        // Set the visibility to GONE when the fragment loads
        if (contact_us_header_bg != null) {
            contact_us_header_bg.setVisibility(View.GONE);
        }

        // Get references to form elements
        final EditText nameEditText = view.findViewById(R.id.nameEditText);
        final EditText emailEditText = view.findViewById(R.id.emailEditText);
        final EditText messageEditText = view.findViewById(R.id.messageEditText);
        Button sendMessageButton = view.findViewById(R.id.sendMessageButton);

        // Handle send message button click
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user inputs
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String message = messageEditText.getText().toString();

                // TODO: Handle the message (e.g., send it to a server or display it)

                // For simplicity, just show a toast message
                // Replace this with your actual logic for handling the message
                String toastMessage = "Name: " + name + "\nEmail: " + email + "\nMessage: " + message;
                // Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Button for closing the fragment
        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the fragment from the fragment container
                getActivity().getSupportFragmentManager().beginTransaction().remove(QuickChatFragment.this).commit();

                // Access LinearLayout from the activity's layout
                LinearLayout ContactText = getActivity().findViewById(R.id.ContactText);
                LinearLayout MainButtonGroup = getActivity().findViewById(R.id.MainButtonGroup);
                ImageView contact_us_header_bg = getActivity().findViewById(R.id.contact_us_header_bg);

                // Set the visibility to VISIBLE when the fragment is being closed
                if (ContactText != null) {
                    ContactText.setVisibility(View.VISIBLE);
                }

                // Set the visibility to VISIBLE when the fragment is being closed
                if (MainButtonGroup != null) {
                    MainButtonGroup.setVisibility(View.VISIBLE);
                }

                // Set the visibility to VISIBLE when the fragment is being closed
                if (contact_us_header_bg != null) {
                    contact_us_header_bg.setVisibility(View.VISIBLE);
                }

            }
        });

        return view;
    }
}
