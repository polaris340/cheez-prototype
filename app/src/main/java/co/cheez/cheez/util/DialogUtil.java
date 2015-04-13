package co.cheez.cheez.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import co.cheez.cheez.App;
import co.cheez.cheez.R;

/**
 * Created by jiho on 4/7/15.
 */
public class DialogUtil {

    public static Dialog getSendMailDialog(final Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_send_mail, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(rootView);

        final EditText titleInput = (EditText) rootView.findViewById(R.id.et_title);
        final EditText nameInput = (EditText) rootView.findViewById(R.id.et_username);
        final EditText emailInput = (EditText) rootView.findViewById(R.id.et_email);
        final EditText bodyInput = (EditText) rootView.findViewById(R.id.et_body);

        final Dialog dialog = builder.create();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_submit:
                        final CharSequence title = titleInput.getText();
                        final CharSequence name = nameInput.getText();
                        final CharSequence email = nameInput.getText();
                        final CharSequence body = bodyInput.getText();

                        if (title.length() == 0) {
                            MessageUtil.showMessage("제목을 입력해주세요");
                            return;
                        }
//                        if (name.length() == 0) {
//                            MessageUtil.showMessage("이름을 입력해주세요");
//                            return;
//                        }
//                        if (email.length() == 0) {
//                            MessageUtil.showMessage("이메일을 입력해주세요");
//                            return;
//                        }
                        if (body.length() == 0) {
                            MessageUtil.showMessage("내용을 입력해주세요");
                            return;
                        }

                        final Dialog progressDialog = new ProgressDialog(context);

                        Request request = new StringRequest(
                                Request.Method.POST,
                                App.BASE_URL + "/sendmail",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        MessageUtil.showMessage("소중한 의견 감사합니다 :)");
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        MessageUtil.showMessage("오류가 발생했습니다. 잠시 후에 다시 시도해주세요 :(");
                                        progressDialog.dismiss();
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String,String> getParams(){
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("name", name.toString());
                                params.put("email", email.toString());
                                params.put("title", title.toString());
                                params.put("body", body.toString());

                                return params;
                            }
                        };

                        progressDialog.show();
                        App.getRequestQueue().add(request);

                        break;
                    case R.id.btn_cancel:
                        dialog.dismiss();
                        break;
                }
            }
        };

        rootView.findViewById(R.id.btn_cancel).setOnClickListener(onClickListener);
        rootView.findViewById(R.id.btn_submit).setOnClickListener(onClickListener);

        return dialog;
    }
}
