package com.hitstreamr.hitstreamrbeta.DrawerMenuFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hitstreamr.hitstreamrbeta.MainActivity;
import com.hitstreamr.hitstreamrbeta.R;

public class GeneralSettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generalsettings, container, false);

        Button close = (Button) view.findViewById(R.id.closeBtn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("TYPE", getArguments().getString("TYPE"));

                startActivity(intent);

            }
        });


        return view;
    }

}
