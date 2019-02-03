package team_h.boostcamp.myapplication.view.memories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import team_h.boostcamp.myapplication.databinding.ItemMemoryCardBinding;
import team_h.boostcamp.myapplication.model.Memory;
import team_h.boostcamp.myapplication.view.adapter.AdapterContract;
import team_h.boostcamp.myapplication.view.adapter.BaseRecyclerViewAdapter;

public class MemoriesCardAdapter extends BaseRecyclerViewAdapter<Memory, MemoriesCardAdapter.ViewHolder>{
    private ViewClickListener mListener;

    public interface ViewClickListener{

        void onPlayButtonClicked(int position);
        void onCloseButtonLicked(int position);
    }

    public MemoriesCardAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMemoryCardBinding binding = ItemMemoryCardBinding
                .inflate(LayoutInflater
                        .from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    public void setOnClickListener(ViewClickListener listener){
        mListener = listener;
    }

    @Override
    protected void onBindView(ViewHolder holder, int position) {
        holder.binding.tvSubTitle.setText(itemList.get(position).getTitle());
        holder.binding.rvDiary.setHasFixedSize(true);
        MemoriesDiaryAdapter adapter = new MemoriesDiaryAdapter(getContext());
        holder.binding.rvDiary.setAdapter(adapter);
        holder.binding.rvDiary.setLayoutManager(new LinearLayoutManager(getContext()));
        //adapter.addItems(itemList.get(position).getMemories());

        if(mListener != null){
            holder.binding.ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPlayButtonClicked(position);
                }
            });

            holder.binding.ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCloseButtonLicked(position);
                }
            });
        }
    }

    /*
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {        //super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.binding.ivClose.setOnClickListener(v ->{
            if(onItemClickListener != null) {
                onItemClickListener.onClickItem(position);
            }
        });

        viewHolder.binding.ivPlay.

        bindViewHolder(viewHolder, position);
    }
    */


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMemoryCardBinding binding;

        ViewHolder(ItemMemoryCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
