package ezbus.mit20550588.passenger.ui.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;

import ezbus.mit20550588.passenger.R;
import ezbus.mit20550588.passenger.data.viewModel.AuthViewModel;
import ezbus.mit20550588.passenger.util.UserStateManager;

public class QuickChatFragment extends Fragment {

    private AuthViewModel authViewModel;

    private UserStateManager userManager;

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


        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe authentication result
        authViewModel.getChatResponse().observe(getViewLifecycleOwner(), chatResponse -> {
            if (chatResponse != null) {
//
                TextView chatResponseTextView = view.findViewById(R.id.quickChatResponseTextView);
                chatResponseTextView.setVisibility(View.VISIBLE);
                view.findViewById(R.id.quickChatLayout).setVisibility(View.GONE);

            }
        });

        // Observe the error message LiveData
        authViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        userManager = UserStateManager.getInstance();
        // Handle send message button click
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user inputs
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String message = messageEditText.getText().toString();

                nameEditText.setError(null);
                emailEditText.setError(null);
                messageEditText.setError(null);

                if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    if (name.isEmpty()) {
                        nameEditText.setError("Name is required");
                    }
                    if (email.isEmpty()) {
                        emailEditText.setError("Email is required");
                    }

                    if (message.isEmpty()) {
                        messageEditText.setError("Message is required");
                    }

                    return;
                } else {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (message.length() < 10) {
                            Toast.makeText(getContext(), "Message must be at least 10 characters long", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Map<String, String> newChat = new HashMap<>();
                            newChat.put("name", name);
                            newChat.put("contactEmail", email);
                            newChat.put("message", message);
                            newChat.put("userEmail", userManager.getUser().getEmail());

                            authViewModel.sendChat(newChat);
                        }
                    }
                }


            }
        });

        // Button for closing the fragment
        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeFragment();


            }
        });

        return view;
    }

    private void closeFragment() {
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
}
