package com.example.tp2_formulaire;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnAdd;
    private Contact  firstContact = new Contact("Janumporn","Malasri", "31/05/1978","0753235890","malasrij@hotmail.com","Villeurbanne","69100", "F",0, null);
    private ArrayList<Contact> listContact = new ArrayList<>();
    private MyAdapter adapter;
    final private String fileName = "ListContact.csv";
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Contact newContact = (Contact) intent.getSerializableExtra(FormActivity.KEY_FORM);
                        listContact.add(newContact);
                        saveFile(fileName,listContact);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = this.findViewById(R.id.listView);
        btnAdd = findViewById(R.id.btnAdd);
        if(!ReadFile(fileName)) {
            this.listContact.add(firstContact);
        }
       // this.listContact.add(firstContact);
        adapter = new MyAdapter(MainActivity.this, listContact);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Détails de Contact")
                        .setIcon(R.mipmap.ic_info_light_blue)
                        .setMessage(listContact.get(position).toString())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });
                //Creating dialog box
                AlertDialog dialog  = builder.create();
                dialog.show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                listContact.remove(position);
                adapter.notifyDataSetChanged();
                saveFile(fileName,listContact);
                return true;
            }
        });

    }
    public void onAddClick(View view) {
        Intent intent = new Intent(MainActivity.this, FormActivity.class);
        mStartForResult.launch(intent);
    }

    private  void saveFile(String fileName, ArrayList<Contact> list){
        FileOutputStream fos = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter writer = null;
        try{
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStreamWriter = new OutputStreamWriter(fos);
            writer = new BufferedWriter(outputStreamWriter);
            Iterator iter = list.iterator();
            Contact contact;
            while (iter.hasNext()) {
                contact = (Contact) iter.next();
                writer.append(contact.getNom());
                writer.append(";");
                writer.append(contact.getPrenom());
                writer.append(";");
                writer.append(contact.getDdn());
                writer.append(";");
                writer.append(contact.getNumTel());
                writer.append(";");
                writer.append(contact.getEmail());
                writer.append(";");
                writer.append(contact.getAddress());
                writer.append(";");
                writer.append(contact.getCodePostal());
                writer.append(";");
                writer.append(contact.getGenre());
                writer.append(";");
                writer.append(String.valueOf(contact.getResId()));
                writer.append(";");
                writer.append(contact.getImageUriStr());
                writer.append("\n");
            }
        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            try {
                writer.flush();
                writer.close();
                outputStreamWriter.close();
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean ReadFile(String fileName){
        File directory = this .getFilesDir();
        File file = new File(directory, fileName);
        FileInputStream fis= null;
        InputStreamReader input = null;
        BufferedReader bReader = null;
        if(file.exists())
        {
            try
            {
                fis = openFileInput(fileName);
                input = new InputStreamReader(fis);
                bReader = new BufferedReader(input);
                String line;
                //clear listContact before adding new data
                listContact.clear();
                while ((line = bReader.readLine()) != null)
                {//Contact(String nom, String prenom, String ddn, String numTel, String email, String address, String codePostal, String genre, Uri imgUri)
                    String nom = line.split(";")[0];
                    String prenom = line.split(";")[1];
                    String ddn = line.split(";")[2];
                    String numTel = line.split(";")[3];
                    String email = line.split(";")[4];
                    String address = line.split(";")[5];
                    String codePostal = line.split(";")[6];
                    String genre = line.split(";")[7];
                  //  int resInt = Integer.parseInt(line.split(";")[8]);
                    String imgUriStr = line.split(";")[9];
                    Contact contact = new Contact(nom,prenom,ddn,numTel,email,address,codePostal,genre, 0, imgUriStr);
                    listContact.add(contact);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    bReader.close();
                    input.close();
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        return false;
    }
}