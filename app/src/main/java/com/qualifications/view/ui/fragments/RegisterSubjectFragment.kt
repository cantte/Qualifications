package com.qualifications.view.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.qualifications.R
import com.qualifications.model.Subject
import com.qualifications.network.ApiCallback
import com.qualifications.network.SessionManager
import com.qualifications.viewmodel.SubjectViewModel
import kotlinx.android.synthetic.main.fragment_register_subject.*
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterSubjectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterSubjectFragment : Fragment() {
    private lateinit var codeField: TextInputLayout
    private lateinit var nameField: TextInputLayout

    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_subject , container , false)
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        subjectViewModel = SubjectViewModel(view.context)
        sessionManager = SessionManager(view.context)

        codeField = view.findViewById(R.id.subject_code_field)
        nameField = view.findViewById(R.id.subject_name_field)

        register_subject_button.setOnClickListener {
            saveSubject()
        }
    }

    private fun saveSubject() {
        register_subject_button.isEnabled = false
        register_subject_button.isClickable = false

        val codeText = codeField.editText?.text.toString()
        val nameText = nameField.editText?.text.toString()

        val subject = Subject(codeText , nameText)

        subject.userId = sessionManager.fetchUserId().toString()

        subjectViewModel.saveSubject(subject , object : ApiCallback<Subject> {
            override fun onFailure(exception: Throwable) {
                register_subject_button.isEnabled = true
                register_subject_button.isClickable = true
            }

            override fun onResponse(result: Response<Subject>) {
                codeField.editText?.text?.clear()
                nameField.editText?.text?.clear()

                register_subject_button.isEnabled = true
                register_subject_button.isClickable = true

                findNavController().navigate(R.id.subjectsFragment)
            }
        })
    }
}