package com.example.nobetcieczaneson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class InfoEczane extends AppCompatActivity {

    TextView eAdi,eTel,eAdres,eAdi2,eTel2,eAdres2,title;

    Button mapB1,mapB2;

    Elements eczaneAdi,telefonNo,adres;

    static String url = "https://apps.istanbulsaglik.gov.tr/NobetciEczane/Home/GetEczaneler";

    private class DownloadData extends AsyncTask<String,String,String> {
        Document doc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Eczane adi,telefon numarasi,ve adresinin gosterilcegi textlerin olusturulmasi,
            eAdi = (TextView) findViewById(R.id.eAdi);
            eTel = (TextView) findViewById(R.id.eTel);
            eAdres = (TextView) findViewById(R.id.eAdres);

            eAdi2 = (TextView) findViewById(R.id.eAdi2);
            eTel2 = (TextView) findViewById(R.id.eTel2);
            eAdres2 = (TextView) findViewById(R.id.eAdres2);

            //Hangi tarihte hangi ilcedeki nobetci eczanelerin gosterildigi kisim.
            title = (TextView) findViewById(R.id.title);

            //Buttonlarin olusturuldugu kisim.
            mapB1=(Button)findViewById(R.id.mapB1);
            mapB2=(Button)findViewById(R.id.mapB2);
        }
        @Override
        protected String doInBackground(String... strings) {

            try {
                doc = Jsoup.connect(url).userAgent("Mozilla").data("ilce", strings[0]).post();
            }
            catch (IOException e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            InfoEczane.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Elements select = doc.select("div[class=card-header text-center]");
                    String text = ((Element) select.get(0)).text();

                    eczaneAdi = doc.select("b"); // Eczane adi aliniyor
                    telefonNo = doc.select("tr>td>label>a"); //Telefon no aliniyor
                    adres= doc.select("tr>td>label"); // Adres

                    String str2 = "\\?";
                    String str3 = "&";
                    String str4 = "=";

                    String map1 = ((Element)doc.select("a[class=btn btn-primary btn-block]").get(0)).attr("href");
                    final double x1 = Double.parseDouble(map1.split(str2)[1].split(str3)[0].split(str4)[1]);
                    final double y1 = Double.parseDouble(map1.split(str2)[1].split(str3)[1].split(str4)[1]);

                    String map2 = ((Element)doc.select("a[class=btn btn-primary btn-block]").get(1)).attr("href");
                    final double x2 = Double.parseDouble(map2.split(str2)[1].split(str3)[0].split(str4)[1]);
                    final double y2 = Double.parseDouble(map2.split(str2)[1].split(str3)[1].split(str4)[1]);

                    title.setText(text);
                    eAdi.setText(eczaneAdi.get(2).text());
                    eTel.setText(telefonNo.get(0).text());
                    eAdres.setText(adres.get(7).text());


                    eAdi2.setText(eczaneAdi.get(5).text());
                    eTel2.setText(telefonNo.get(2).text());
                    eAdres2.setText(adres.get(17).text());

                    mapB1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(InfoEczane.this,MapsActivity.class);
                            intent.putExtra("eczaneadi",eczaneAdi.get(2).text());
                            intent.putExtra("coorx",x1);
                            intent.putExtra("coory",y1);
                            InfoEczane.this.startActivity(intent);
                        }
                    });
                    mapB2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(InfoEczane.this,MapsActivity.class);
                            intent.putExtra("eczaneadi",eczaneAdi.get(5).text());
                            intent.putExtra("coorx",x2);
                            intent.putExtra("coory",y2);
                            InfoEczane.this.startActivity(intent);
                        }
                    });
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_eczane);
        String ilceismi= getIntent().getStringExtra("ilce");
        new DownloadData().execute(new String[]{ilceismi});
    }
}
