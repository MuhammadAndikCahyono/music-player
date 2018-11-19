package com.github.anrimian.musicplayer.ui.common.order.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.anrimian.musicplayer.domain.models.composition.Order;
import com.github.anrimian.musicplayer.ui.utils.OnItemClickListener;

import java.util.List;

import static java.util.Arrays.asList;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private List<Order> orderList = asList(Order.values());

    private OnItemClickListener<Order> onItemClickListener;

    private Order selectedOrder;

    public OrderAdapter(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      return new OrderViewHolder(inflater, parent, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bindView(order, selectedOrder == order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOnItemClickListener(OnItemClickListener<Order> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
