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
import com.google.android.material.card.MaterialCardView;
import com.techfix.app.R;
import com.techfix.app.ui.booking.BookRepairActivity;
import com.techfix.app.ui.branches.BranchesMapActivity;
import com.techfix.app.ui.tracking.RepairTrackingActivity;
import com.techfix.app.utils.SessionManager;

public class HomeFragment extends Fragment {

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());

        TextView tvGreeting = root.findViewById(R.id.tvGreeting);
        tvGreeting.setText("Hello, " + sessionManager.getFullName() + "!");

        MaterialButton btnQuickBook = root.findViewById(R.id.btnQuickBook);
        MaterialCardView cardBookRepair = root.findViewById(R.id.cardBookRepair);
        MaterialCardView cardTrackRepair = root.findViewById(R.id.cardTrackRepair);
        MaterialCardView cardViewServices = root.findViewById(R.id.cardViewServices);
        MaterialCardView cardFindBranches = root.findViewById(R.id.cardFindBranches);

        // Click listeners for Quick Actions
        View.OnClickListener openBookingListener = v -> {
            Intent intent = new Intent(requireContext(), BookRepairActivity.class);
            startActivity(intent);
        };

        btnQuickBook.setOnClickListener(openBookingListener);
        cardBookRepair.setOnClickListener(openBookingListener);

        cardTrackRepair.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RepairTrackingActivity.class);
            startActivity(intent);
        });

        cardViewServices.setOnClickListener(v -> {
            if (getActivity() instanceof CustomerMainActivity) {
                ((CustomerMainActivity) getActivity()).switchToTab(R.id.nav_services);
            }
        });

        cardFindBranches.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BranchesMapActivity.class);
            startActivity(intent);
        });

        return root;
    }
}
