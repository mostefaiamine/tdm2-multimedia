package dz.esi.multimedia.fragments


import android.os.Bundle
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.MediaController
import android.widget.Toolbar
import android.widget.VideoView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout

import dz.esi.multimedia.R


/**
 * A simple [Fragment] subclass.
 */
class VideoFragment : Fragment() {

    private var mc: MediaController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_video, container, false)
        val btn = rootView.findViewById<Button>(R.id.btnPlayVideo)
        btn.setOnClickListener { lireVideo(R.raw.video) }
        return rootView
    }

    private fun lireVideo(resId: Int) {
        val vv = view!!.findViewById<VideoView>(R.id.videoPlayer)
        if (mc == null) {

            mc = MediaController(activity)
            vv.setMediaController(mc)
            val video = Uri.parse("android.resource://" + activity?.packageName + "/"
                    + resId) //do not add any extension
            vv.setVideoURI(video)
            vv.start()
        } else {
            if (!vv.isPlaying) {
                vv.start()

            } else {
                vv.pause()
            }
        }
    }

}// Required empty public constructor
