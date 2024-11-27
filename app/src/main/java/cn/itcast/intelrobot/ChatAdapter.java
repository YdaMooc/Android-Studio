package cn.itcast.intelrobot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<ChatBean> chatBeanList; //聊天数据
    private Context mContext;
    public ChatAdapter(Context context) {
        this.mContext = context;
    }
    /**
     * 获取数据，更新界面
     */
    public void setData(List<ChatBean> chatBeanList) {
        this.chatBeanList = chatBeanList;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        MyViewHolder holder = null;
        //判断当前的信息是发送的信息还是接收到的信息，不同信息加载不同的view
        if (viewType == ChatBean.RECEIVE) {
            //加载左边布局，也就是机器人对应的布局信息
            itemView = LayoutInflater.from(mContext).inflate(R.layout.
                    chatting_left_item, parent, false);
        } else if (viewType == ChatBean.SEND) {
            //加载右边布局，也就是用户对应的布局信息
            itemView = LayoutInflater.from(mContext).inflate(R.layout.
                    chatting_right_item, parent, false);
        }
        holder = new MyViewHolder(itemView);
        return holder;
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
        int type = 0;
        //判断当前的信息是发送的信息还是接收到的信息
        if (chatBeanList.get(position).getState() == ChatBean.RECEIVE) {
            type = ChatBean.RECEIVE;//条目显示的类型为接收到消息的类型
        } else if (chatBeanList.get(position).getState() == ChatBean.SEND) {
            type = ChatBean.SEND;   //条目显示的类型为发送消息的类型
        }
        return type;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_chat_content; //聊天内容
        public MyViewHolder(View view) {
            super(view);
            tv_chat_content = view.findViewById(R.id.tv_chat_content);
        }
    }
}

