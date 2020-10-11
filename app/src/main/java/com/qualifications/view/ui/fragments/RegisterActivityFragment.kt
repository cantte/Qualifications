package com.qualifications.view.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.qualifications.R
import com.qualifications.model.Activity
import com.qualifications.model.Qualification
import com.qualifications.viewmodel.SubjectViewModel
import kotlinx.android.synthetic.main.fragment_register_activity.*

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterActivityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterActivityFragment : DialogFragment() {
    private lateinit var qualification: Qualification

    private lateinit var nameField: TextInputLayout
    private lateinit var noteField: TextInputLayout
    private lateinit var percentField: TextInputLayout

    private lateinit var subjectViewModel: SubjectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qualification = arguments?.getSerializable("qualification") as Qualification

        subjectViewModel = SubjectViewModel(view.context)

        nameField = view.findViewById(R.id.activity_name_field)
        noteField = view.findViewById(R.id.activity_note_field)
        percentField = view.findViewById(R.id.activity_percent_field)

        register_activity_button.setOnClickListener {
            saveActivity()
        }
    }

    private fun saveActivity() {
        val nameText = nameField.editText?.text.toString()
        val noteText = noteField.editText?.text.toString().toFloat()
        val percentText = percentField.editText?.text.toString().toFloat()

        val activity = Activity()
        activity.name = nameText
        activity.note = noteText
        activity.percent = percentText

        if (qualification.addActivity(activity)) {
            if (subjectViewModel.saveActivity(activity, qualification.id)) {
                dismiss()
            }
        }
    }
}