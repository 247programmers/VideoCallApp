package com.sjvvcc.sjvc5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sjvvcc.sjvc5.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*   // 요게 find view안써도 되게 하는 것...


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var array :  MutableList<UserDTO> = arrayListOf()
    var uids : MutableList<String> = arrayListOf()
    val myUid = FirebaseAuth.getInstance().uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        //var scKey_text = findViewById<EditText>(R.id.scKey)  <--이거안해도됨
        var secret_key_input = scKey.text   // secret key input
        val st1 = "emn"
        val st2 = "eh"
        val secret_correct_code = st1 + st2
        val correct_Congraturation = "❤❤❤PERFECT❤❤❤"


        var scKey_btn_submit = scKey_btn // button


        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener {
            task ->
            array.clear()
            uids.clear()
            for (item in task.result!!.documents ){
                if(myUid != item.id){
                    array.add(item.toObject(UserDTO::class.java)!!)
                    uids.add(item.id)
                }


            }
            binding.peopleListRecyclerview.adapter?.notifyDataSetChanged()
            //println("TEST :  ${array}")
        }
        binding.peopleListRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.peopleListRecyclerview.adapter = RecyclerviewAdapter()
        watchingMyUidVideoRequest()


        // SCRET KEY
        scKey_btn_submit.setOnClickListener{

            //correct to do
            if (  secret_key_input.toString().equals(secret_correct_code)  ){
                println("success")
                Secret_Sentence.setText(correct_Congraturation)





            }
            else {
                println("u put ${secret_key_input.toString()}")
                println("fail ")
            }
        }

    }
    fun watchingMyUidVideoRequest(){
        val myUid = FirebaseAuth.getInstance().uid
        FirebaseFirestore.getInstance().collection("users").document(myUid!!).addSnapshotListener { value, error ->
            var userDTO = value?.toObject(UserDTO::class.java)
                if(userDTO?.channel != null){
                    showJoinDialog(userDTO.channel!!)

            }

        }

    }
    fun showJoinDialog(channel: String){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("${channel} do you wanna join this room?")
        builder.setPositiveButton("Yes"){
            dialogInterface,i ->
            openVideoActivity(channel)
            removeChannelStr()
        }
        builder.setNegativeButton("No"){dialogInterface, i ->
            dialogInterface.dismiss()

        }
        builder.create().show()


    }

    fun removeChannelStr(){

        var map = mutableMapOf<String, Any>()
        map["channel"]= FieldValue.delete()
        FirebaseFirestore.getInstance().collection("users").document(myUid!!).update(map)

    }


    inner class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person,parent,false)
            return ViewHolder(view)
            //TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            return array.size
            //TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemEmail.text = array[position].email
            holder.itemView.setOnClickListener{
                var channelNumber = (1000..1000000).random().toString()
                openVideoActivity(channelNumber)
                createVideoChatRoom(position,channelNumber)

                //openVideoActivity("howlab")
            }
            //TODO("Not yet implemented")
        }
        inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
            val itemEmail = view.findViewById<TextView>(R.id.item_email)

        }

    }

    fun openVideoActivity(channelId : String){
        val i = Intent( this,VideoActivity::class.java)
        i.putExtra("channelID",channelId)
        startActivity(i)
    }
    fun createVideoChatRoom(position: Int , channel : String){
        var map = mutableMapOf<String,Any>()
        map["channel"] = channel
        FirebaseFirestore.getInstance().collection("users").document(uids[position]).update(map)

    }

}