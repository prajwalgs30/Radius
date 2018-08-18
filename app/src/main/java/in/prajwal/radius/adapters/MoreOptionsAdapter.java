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

public class MoreOptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<FacilityOption> moreOptionList;
    private Context mContext;
    private int focusedItem = 0;
    private String type;

    public MoreOptionsAdapter(Context context, List<FacilityOption> moreOptionList, String type) {
        this.moreOptionList = moreOptionList;
        this.mContext = context;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {
        View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_layout_more_options, viewGroup, false);
        return new ListRowViewHolder(menuItemLayoutView, mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ListRowViewHolder listRowViewHolder = (ListRowViewHolder) holder;
        listRowViewHolder.itemView.setSelected(focusedItem == position);
        listRowViewHolder.getLayoutPosition();

        FacilityOption facilityOption = moreOptionList.get(position);
        listRowViewHolder.moreOptionName.setText(facilityOption.getOptionName());
        int resId = mContext.getResources().getIdentifier(facilityOption.getOptionIcon().replace("-","_"), "drawable", mContext.getPackageName());
        listRowViewHolder.moreOptionIcon.setImageResource(resId);

        if(type.equals("rooms")) {
            if (MainActivity.SELECTED_ROOM == position){
                listRowViewHolder.selectedIndicator.setVisibility(View.VISIBLE);
            }else{
                listRowViewHolder.selectedIndicator.setVisibility(View.GONE);
            }
        }else{
            if (MainActivity.SELECTED_OTHER == position){
                listRowViewHolder.selectedIndicator.setVisibility(View.VISIBLE);
            }else{
                listRowViewHolder.selectedIndicator.setVisibility(View.GONE);
            }
        }
    }


    public void clearAdapter() {
        moreOptionList.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != moreOptionList ? moreOptionList.size() : 0);
    }

    public class ListRowViewHolder extends RecyclerView.ViewHolder {

        private TextView moreOptionName;
        private LayoutInflater inflater;
        private RelativeLayout moreOptionRel;
        private ImageView moreOptionIcon, selectedIndicator;

        public ListRowViewHolder(View view, final Context context) {
            super(view);
            inflater = LayoutInflater.from(mContext);
            this.moreOptionName = view.findViewById(R.id.moreOptionName);
            this.moreOptionRel = view.findViewById(R.id.moreOptionRel);
            this.moreOptionIcon =  view.findViewById(R.id.moreOptionIcon);
            this.selectedIndicator = view.findViewById(R.id.selectedIndicator);

            this.moreOptionRel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type.equals("rooms")){
                        ((MainActivity) mContext).roomKey = moreOptionList.get(getPosition()).getId();
                        MainActivity.SELECTED_ROOM = getPosition();
                    }else{
                        ((MainActivity) mContext).otherKey = moreOptionList.get(getPosition()).getId();
                        MainActivity.SELECTED_OTHER = getPosition();
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }
}
