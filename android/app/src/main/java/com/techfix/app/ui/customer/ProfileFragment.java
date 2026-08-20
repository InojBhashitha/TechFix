package com.techfix.app.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.data.remote.ApiClient;
import com.techfix.app.ui.auth.LoginActivity;
import com.techfix.app.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());

        TextView tvName = root.findViewById(R.id.tvProfileName);
        TextView tvEmail = root.findViewById(R.id.tvProfileEmail);
        MaterialButton btnLogout = root.findViewById(R.id.btnLogout);

        tvName.setText(sessionManager.getFullName());
        tvEmail.setText(sessionManager.getEmail());

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            ApiClient.setAuthToken(null);
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return root;
    }
}
