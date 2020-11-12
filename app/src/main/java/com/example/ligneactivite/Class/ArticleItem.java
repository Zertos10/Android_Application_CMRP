package com.example.ligneactivite.Class;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.net.URL;

public class ArticleItem   {

private String titre= "Titre par defaut";
private String descrip = "Description par défaut";
private String url = "http://www.google.fr";
private String imageUrl = "https://www.cmrp.fr/img/logo-cmrp-bloc.png";


public ArticleItem(){}

public ArticleItem(String titre ,String descrip,String url ,String imageUrl)
    {

        this.titre = titre;
        this.descrip = descrip;
        this.url = url;
        this.imageUrl = imageUrl;

    }
public String getTitre() {return titre;}
public String getDescrib() {return descrip;}
public String getUrl() {return url;}
public String getImageUrl() {return imageUrl;}

public void add(String title,String descrip,String url,String imageUrl)
{
    this.titre = titre;
    this.descrip = descrip;
    this.url = url;
    this.imageUrl = imageUrl;
}
public  void setTitle(String title){this.titre = title;}
public void  setDescription(String descrip){this.descrip = descrip;}
public void  setUrl(String url){this.url = url;}
public void  setImageUrl(String imageUrl){this.imageUrl = imageUrl;}

}
