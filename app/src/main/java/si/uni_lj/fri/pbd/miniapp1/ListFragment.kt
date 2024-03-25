package si.uni_lj.fri.pbd.miniapp1

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentListBinding
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class ListFragment : Fragment(), RecyclerAdapter.OnItemClickListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null

    private val memoList = ArrayList<MemoModel>()

    private var memosLoaded = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.fab.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_listFragment_to_newFragment)
        }


        recyclerView = binding.recView
        layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager

        if (!memosLoaded) {
            loadMemosFromSharedPreferences()
            memosLoaded = true
        }

        adapter = RecyclerAdapter(memoList,this)
        recyclerView?.adapter = adapter

        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMemosFromSharedPreferences() {
        // Retrieves a reference to the SharedPreferences object named "MemoPrefs"
        val sharedPreferences = requireActivity().getSharedPreferences("MemoPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        sharedPreferences.all.values.forEach { memoJson -> // This line iterates over all key-value pairs stored in the SharedPreferences
            val memo = gson.fromJson(memoJson.toString(), MemoModel::class.java) // Converts the JSON string to a MemoModel
            memoList.add(memo) // Adds the MemoModel object to memoList
        }

        recyclerView?.adapter?.notifyDataSetChanged() // Notify the adapter that data has changed
    }




    override fun onItemClick(position: Int, memo: MemoModel) {

        val bundle = Bundle().apply {
            putString("image", memo.imageBase64)
            putString("title", memo.title)
            putString("details", memo.description)
            putString("timestamp", memo.timestamp)
            putInt("position", position)

        }
        Navigation.findNavController(binding.root).navigate(R.id.action_listFragment_to_detailsFragment, bundle)

    }
}

