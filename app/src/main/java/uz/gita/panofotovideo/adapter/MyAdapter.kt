package uz.gita.panofotovideo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import uz.gita.panofotovideo.R
import uz.gita.panofotovideo.databinding.RecycleItemBinding

/**
 * Author: Zukhriddin Kamolov
 * Date: 27-Apr-24, 11:59 AM.
 * Project: PanoramaViewer
 */
class MyAdapter(val onClick: (String) -> Unit) : androidx.recyclerview.widget.ListAdapter<MyData, MyAdapter.MyViewHolder>(diff) {


    object diff : ItemCallback<MyData>() {
        override fun areItemsTheSame(oldItem: MyData, newItem: MyData): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MyData, newItem: MyData): Boolean =
            oldItem.url == newItem.url

    }

    inner class MyViewHolder(val binding: RecycleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.nameTV.text = currentList[position].name

            if(currentList[position].type == MediaType.Photo) binding.imageIV.setImageResource(R.drawable.ic_photo_library_24)
            else binding.imageIV.setImageResource(R.drawable.ic_video_library_24)

            binding.showB.setOnClickListener {
                onClick.invoke(currentList[position].url)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(RecycleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }
}