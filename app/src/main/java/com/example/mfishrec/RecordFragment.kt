package com.example.mfishrec

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mfishrec.data.RecDatabase
import com.example.mfishrec.data.Record
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.record_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordFragment : Fragment() {
    var records: List<Record>? = listOf()
    var adapter: RecordAdapter? = null

    companion object{
        val instance : RecordFragment by lazy {
            RecordFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_record, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview.layoutManager = LinearLayoutManager(context)
        recyclerview.setHasFixedSize(true)
        adapter = RecordAdapter()
        recyclerview.adapter = adapter

        loadDB()
    }

    fun loadDB(){
        CoroutineScope(Dispatchers.IO).launch {
            records = context?.let {
                RecDatabase.getInstance(it)?.recordDao()?.getAll()
            }
            activity?.runOnUiThread {
                records?.let {
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    inner class RecordAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        private val TYPE_NORMAL = 0
        private val TYPE_EMPTY = 1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when(viewType){
                TYPE_NORMAL -> {
                    return RecordHolder(LayoutInflater.from(parent.context).inflate(R.layout.record_item,parent,false))
                }else -> {
                    return EmptyDataHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_item,parent,false))
                }
            }
        }

        override fun getItemCount(): Int {
            if(records?.size == 0)
                return 1
            else
                return records?.size!!
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(holder){
                is RecordHolder -> {
                    var model = records?.get(position)
                    holder.imageView.setImageURI(Uri.parse(model?.imguri))
                    holder.dateText.text = model?.date
                    holder.timeText.text = model?.time
                    holder.layout.setOnClickListener {
                        model?.let { openRecord(it) }
                    }
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            if(records?.size == 0)
                return TYPE_EMPTY
            else
                return TYPE_NORMAL
        }

        private fun openRecord(record:Record){
            var bundle = Bundle()
            bundle.putString("type","record")
            bundle.putSerializable("record",record)

            var intent = Intent(context,ShowActivity::class.java)
            intent.putExtra("bundle",bundle)
            startActivity(intent)
        }

    }

    class EmptyDataHolder(view: View):RecyclerView.ViewHolder(view){
    }

    class RecordHolder(view: View) : RecyclerView.ViewHolder(view){
        var layout = view.record_item_layout
        var imageView = view.record_item_imageView
        var dateText = view.record_item_date
        var timeText = view.record_item_time
    }
}