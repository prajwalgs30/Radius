package in.prajwal.radius.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.prajwal.radius.R;
import in.prajwal.radius.activities.MainActivity;
import in.prajwal.radius.model.FacilityOption;

/**
 * Created by prajwalgs on 18/08/18.
 */

public class PropertyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<FacilityOption> propertyList;
    private Context mContext;
    private int focusedItem = 0;

    public PropertyAdapter(Context context, List<FacilityOption> propertyList) {
        this.propertyList = propertyList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_layout_property, viewGroup, false);
        return new ListRowViewHolder(menuItemLayoutView, mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ListRowViewHolder listRowViewHolder = (ListRowViewHolder) holder;
        listRowViewHolder.itemView.setSelected(focusedItem == position);
        listRowViewHolder.getLayoutPosition();

        FacilityOption facilityOption = propertyList.get(position);
        listRowViewHolder.propertyName.setText(facilityOption.getOptionName());
        int resId = mContext.getResources().getIdentifier(facilityOption.getOptionIcon(), "drawable", mContext.getPackageName());
        listRowViewHolder.propertyIcon.setImageResource(resId);
    }


    public void clearAdapter() {
        propertyList.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != propertyList ? propertyList.size() : 0);
    }

    public class ListRowViewHolder extends RecyclerView.ViewHolder {

        private TextView propertyName;
        private LayoutInflater inflater;
        private RelativeLayout propertyRel;
        private ImageView propertyIcon;

        public ListRowViewHolder(View view, final Context context) {
            super(view);
            inflater = LayoutInflater.from(mContext);
            this.propertyName = (TextView) view.findViewById(R.id.propertyName);
            this.propertyRel = (RelativeLayout) view.findViewById(R.id.propertyRel);
            this.propertyIcon = (ImageView) view.findViewById(R.id.propertyIcon);

            this.propertyRel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mContext).openMoreInfo(propertyList.get(getPosition()).getId());
                    MainActivity.SELECTED_PROPERTY = getPosition();
                }
            });
        }
    }
}
