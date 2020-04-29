package dz.esi.multimedia.fragments


import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

import dz.esi.multimedia.R
import android.graphics.Bitmap
import android.app.Activity.RESULT_OK
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_camera.*


/**
 * A simple [Fragment] subclass.
 */
class CameraFragment : Fragment() {

    private val REQUEST_CAPTURE_IMAGE = 100


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_camera, container, false)
        val btn = rootView.findViewById<Button>(R.id.btnTakePhoto)
        btn.setOnClickListener { lancerCamera() }
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                            data: Intent?) {
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.extras != null) {
                val imageBitmap = data.extras!!.get("data") as Bitmap
                imgCamera.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun lancerCamera() {
        val pictureIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
        )
        if (activity?.getPackageManager()?.let { pictureIntent.resolveActivity(it) } != null) {
            startActivityForResult(pictureIntent,
                    REQUEST_CAPTURE_IMAGE)
        }
    }





}// Required empty public constructor
