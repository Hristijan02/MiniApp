package si.uni_lj.fri.pbd.miniapp1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import si.uni_lj.fri.pbd.miniapp1.databinding.FragmentNewBinding
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.ExperimentalEncodingApi
import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NewFragment : Fragment() {

    private var request_code: Int = 123 //  Used to uniquely identify requests made to the system
    private val CAMERA_PERMISSION_REQUEST_CODE = 100 // Represents a request code specifically used for handling camera permission requests
    private val CAMERA_PERMISSION = android.Manifest.permission.CAMERA //  Requesting runtime permissions in Android app

    private var _binding: FragmentNewBinding? = null
    private val binding get() = _binding!!

    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewBinding.inflate(inflater, container, false)

        binding.imageView.setImageResource(R.drawable.placeholder)

        val takePhotoButton = binding.root.findViewById<MaterialButton>(R.id.button)
        takePhotoButton.setOnClickListener {
            requestCameraPermission()
        }

        requireContext().getSharedPreferences("myPref", Context.MODE_PRIVATE)

        binding.save.setOnClickListener {

            val title = binding.title.text.toString()
            val description = binding.description.text.toString()

            if (title.isEmpty() && description.isEmpty()) {
                binding.title.error = "Title cannot be empty"
                binding.description.error = "Description cannot be empty"
            }
            else if (title.isEmpty()) {
                binding.title.error = "Title cannot be empty"
            } else if (description.isEmpty()) {
                binding.description.error = "Description cannot be empty"
            } else {
                val dateformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

                val bitmap = imageBitmap
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream) // Compresses the bitmap image (bitmap) into a JPEG format
                val imageBytes = byteArrayOutputStream.toByteArray() // Converts the data written to byteArrayOutputStream into a byte array.
                val imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT) // Encodes the byte array (imageBytes) into a Base64-encoded string.
                val sharedPreferences =
                    requireActivity().getSharedPreferences("MemoPrefs", Context.MODE_PRIVATE)  // Retrieves a reference to the SharedPreferences object named "MemoPrefs"

                val timestamp = dateformat.format(Date())
                if (!sharedPreferences.contains(timestamp)) {
                    val memo = MemoModel(title, description, imageBase64, timestamp)
                    val gson = Gson()
                    val memoJson = gson.toJson(memo)
                    val editor = sharedPreferences.edit()
                    editor.putString(timestamp, memoJson)
                    editor.apply()
                }
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_newFragment_to_listFragment)
            }
        }

        return binding.root
    }


    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                CAMERA_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            requestPermissions(
                arrayOf(CAMERA_PERMISSION),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed with taking photo
            takePhoto()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with taking photo
                takePhoto()
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(
                    requireContext(),
                    "Camera permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

   fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent,request_code)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == request_code && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap
            imageBitmap = bitmap
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        imageBitmap?.let {
            outState.putParcelable("bitmapKey", it)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        imageBitmap = savedInstanceState?.getParcelable("bitmapKey")
        imageBitmap?.let {
            binding.imageView.setImageBitmap(it)
        }
    }

}