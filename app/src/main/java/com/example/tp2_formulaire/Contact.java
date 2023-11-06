package com.example.tp2_formulaire;

import android.app.Application;
import android.net.Uri;

import java.io.Serializable;

public class Contact implements Serializable {
    private String nom;
    private String prenom;
    private String ddn;
    private String numTel;
    private String email;
    private String address;
    private String codePostal;
    private String genre;
    private int resId;
    private String imageUriStr;

    public Contact(String nom, String prenom, String ddn, String numTel, String email, String address, String codePostal, String genre, int resId, String imgUriStr)  {
        this.nom = nom;
        this.prenom = prenom;
        this.ddn = ddn;
        this.numTel = numTel;
        this.email = email;
        this.address = address;
        this.codePostal = codePostal;
        this.genre = genre;
        this.resId = resId;
        this.imageUriStr = imgUriStr;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getDdn() {
        return ddn;
    }

    public String getNumTel() {
        return numTel;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public int getResId() {
        return resId;
    }

    public String getGenre() {
        return genre;
    }

    public String getImageUriStr() {
        return imageUriStr;
    }

    public void setImageUriStr(String imageUriStr) {
        this.imageUriStr = imageUriStr;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    @Override
    public String toString() {
        return  "Nom=" + nom + '\n' +
                "Prenom=" + prenom + '\n' +
                "Genre=" + genre + '\n' +
                "Date de naissance=" + ddn + '\n' +
                "Numéro de téléphone=" + numTel + '\n' +
                "Email=" + email + '\n' +
                "Adresse=" + address + '\n' +
                "Code Postal=" + codePostal;
    }
}
