package com.tyq.readprogress;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tyq.readprogress.Adapter.SearchAdapter;
import com.tyq.readprogress.bean.Book;
import com.tyq.readprogress.net.BaseAsyncHttp;
import com.tyq.readprogress.net.HttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tyq on 2015/9/5.
 */
public class SearchAcitvity extends Activity {
    private ListView listView;
    private EditText et_search;
    private RelativeLayout btn_search;
    private List<Book> mBooks = new ArrayList<Book>();
    private SearchAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        et_search = (EditText) findViewById(R.id.et_search_content);
        btn_search = (RelativeLayout) findViewById(R.id.rl_search_btn);
        listView = (ListView) findViewById(R.id.lv_search);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.closeKeyBoard(SearchAcitvity.this);
                getRequestData(et_search.getText().toString());
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals("")){
                    btn_search.setVisibility(View.GONE);
                }else {
                    btn_search.setVisibility(View.VISIBLE);
                }
            }
        });



        mAdapter = new SearchAdapter(mBooks,this);
        listView.setAdapter(mAdapter);


    }


    public void getRequestData(String str){
        RequestParams params = new RequestParams();
        params.put("q",str.trim());
        BaseAsyncHttp.getReq("/v2/book/search", params, new HttpResponseHandler() {

            @Override
            public void jsonSuccess(JSONObject resp)  {
                mBooks.clear();

                JSONArray jsonbooks = resp.optJSONArray("books");
                for (int i = 0; i < jsonbooks.length(); i++) {

                    try {
                        Book mBook = new Book();
                        mBook.setTitle(jsonbooks.optJSONObject(i).getString("title"));
                        String author = "";
                        for (int j=0;j<jsonbooks.optJSONObject(i).optJSONArray("author").length();j++){
                            author = author + " " +jsonbooks.optJSONObject(i).optJSONArray("author").optString(j);
                        }
                        mBook.setAuthor(author);
                        mBook.setBitmap(jsonbooks.optJSONObject(i).getString("image"));
                        mBooks.add(mBook);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                updateToView();
            }

            @Override
            public void jsonFail(JSONObject resp) {
                Toast.makeText(SearchAcitvity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateToView(){
        mAdapter.setData(mBooks);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setHomeButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}