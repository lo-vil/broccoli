package com.flauschcode.broccoli.seasons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.RecyclerViewAdapter;

import java.time.Month;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class MonthFragment extends Fragment {

    @Inject
    SeasonalCalendarHolder seasonalCalendarHolder;

    private final Month month;

    public MonthFragment(Month month) {
        super();
        this.month = month;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_month, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ListAdapter<SeasonalFood, RecyclerViewAdapter<SeasonalFood>.Holder> adapter = new RecyclerViewAdapter<SeasonalFood>() {
            @Override
            protected int getLayoutResourceId() {
                return R.layout.seasonal_food_item;
            }

            @Override
            protected int getBindingVariableId() {
                return BR.seasonalFood;
            }

            @Override
            protected void onItemClick(SeasonalFood item) {
                // TODO search
            }
        };
        recyclerView.setAdapter(adapter);

        seasonalCalendarHolder.get().ifPresent(seasonalCalendar -> adapter.submitList(seasonalCalendar.getSeasonalFoodFor(month)));

        return root;
    }

    @BindingAdapter("months")
    public static void bind(LinearLayout layout, List<Month> months) {
        Arrays.stream(Month.values()).forEach(otherMonth -> {
            View.inflate(layout.getContext(), R.layout.seasonal_icon, layout);
            ImageView imageView = (ImageView) layout.getChildAt(otherMonth.getValue()-1);
            if (months.contains(otherMonth)) {
                imageView.setImageTintList(ContextCompat.getColorStateList(BroccoliApplication.getContext(), R.color.colorPrimary));
            }
        });

    }
}