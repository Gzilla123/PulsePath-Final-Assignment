package com.pulsepath.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.pulsepath.databinding.FragmentProfileBinding
import com.pulsepath.ui.auth.AuthActivity
import com.pulsepath.viewmodel.DashboardViewModel

class ProfileFragment : Fragment() {
    private var _b: FragmentProfileBinding? = null
    private val b get() = _b!!
    private val vm: DashboardViewModel by viewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentProfileBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        val user = FirebaseAuth.getInstance().currentUser
        b.tvProfileName.text  = user?.displayName ?: "Athlete"
        b.tvProfileEmail.text = user?.email ?: ""

        vm.totalWorkouts.observe(viewLifecycleOwner)  { b.tvTotalWorkouts.text = (it ?: 0).toString() }
        vm.totalCalories.observe(viewLifecycleOwner)  { b.tvTotalCalories.text = (it ?: 0).toString() }
        vm.totalSteps.observe(viewLifecycleOwner)     { b.tvTotalSteps.text    = (it ?: 0).toString() }

        b.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
