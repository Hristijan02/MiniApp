package si.uni_lj.fri.pbd.miniapp1

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentDetailsBinding
import java.io.ByteArrayOutputStream



class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private var isPlaceHolder = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var itemImage: ImageView? = view.findViewById(R.id.imageView)
        var itemTitle: TextView? = view.findViewById(R.id.title)
        var itemDetail: TextView? = view.findViewById(R.id.details)
        var timestampDetial: TextView? = view.findViewById(R.id.timestamp)

        val image: String? = arguments?.getString("image")
        val title: String? = arguments?.getString("title")
        val details: String? = arguments?.getString("details")
        val timestamp: String? = arguments?.getString("timestamp")

        timestampDetial?.text = timestamp
        itemDetail?.text = details
        itemTitle?.text = title
        if (image != null) {
            val imageBytes = Base64.decode(image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            if (bitmap == null) {
                itemImage?.setImageResource(R.drawable.placeholder) // Clear the imageView
                isPlaceHolder = true
            } else
                itemImage?.setImageBitmap(bitmap)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val title: String? = arguments?.getString("title")
        val details: String? = arguments?.getString("details")
        val timestamp: String? = arguments?.getString("timestamp")

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)


        binding.share.setOnClickListener() {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "message/rfc822" // Specify MIME type for email
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, title)
            emailIntent.putExtra(Intent.EXTRA_TEXT, "$details\n\n$timestamp")
            val bitmapDrawable = binding.imageView.drawable as BitmapDrawable?
            val bitmap = bitmapDrawable?.bitmap

            if (bitmap != null && !isPlaceHolder) {
                // Get the URI of the bitmap image
                val uri = getImageUri(requireContext(), bitmap)

                // Add the image as an attachment
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //  Granting read permission to the email intent
            }
            val chooser = Intent.createChooser(emailIntent, "Send Email")     // Start the email activity with a chooser
            startActivity(chooser)
        }


        binding.delete.setOnClickListener {
            val sharedPreferences =
                requireActivity().getSharedPreferences("MemoPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove(timestamp) // Remove an entry from SharedPreferences using a key
            editor.apply()
            Navigation.findNavController(binding.root).navigate(R.id.action_detailsFragment_to_listFragment)
        }

        return binding.root
    }

    private fun getImageUri(context: Context, bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes) //  Compresses the bitmap image into a JPEG format and writes the compressed data
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Image Description", null)
        return Uri.parse(path)
    }


}