package teamh.boostcamp.myapplication.view.recall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import teamh.boostcamp.myapplication.data.model.Recall;
import teamh.boostcamp.myapplication.databinding.ItemRecallListBinding;

public class RecallListAdapter extends RecyclerView.Adapter<RecallListAdapter.ViewHolder> {

    private Context context;
    private List<Recall> itemList;

    public RecallListAdapter(Context context) {
        this.context = context;
        this.itemList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecallListBinding binding = ItemRecallListBinding.inflate(LayoutInflater.from(context),
                parent,
                false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.tvSubTitle.setText(itemList.get(position).getStartDate()+"~"+itemList.get(position).getEndDate());

        // Fixme recallItem 속의 child recyclerView init
        holder.binding.rvDiary.setLayoutManager(new LinearLayoutManager(context));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateItems(List<Recall> items) {
        this.itemList.clear();
        this.itemList.addAll(items);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemRecallListBinding binding;

        ViewHolder(@NonNull ItemRecallListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

}
