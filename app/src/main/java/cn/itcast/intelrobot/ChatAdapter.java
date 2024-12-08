package cn.itcast.intelrobot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<ChatBean> chatBeanList; // 聊天数据
    private Context mContext;

    public ChatAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<ChatBean> chatBeanList) {
        this.chatBeanList = chatBeanList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == ChatBean.RECEIVE) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.chatting_left_item, parent, false);
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.chatting_right_item, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_chat_content.setText(chatBeanList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return chatBeanList == null ? 0 : chatBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatBeanList.get(position).getState();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_chat_content;

        public MyViewHolder(View view) {
            super(view);
            tv_chat_content = view.findViewById(R.id.tv_chat_content);
        }
    }
}
