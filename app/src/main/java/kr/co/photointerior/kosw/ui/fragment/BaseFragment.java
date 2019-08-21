package kr.co.photointerior.kosw.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import kr.co.photointerior.kosw.ui.BaseActivity;

/**
 * 화면을 구성하는 각 하위 Fragment가 상속받아야 하는 클래스.
 * Created by kugie on 2018. 4. 30.
 */
public abstract class BaseFragment extends Fragment {
    protected BaseActivity mActivity;
    protected View mRootView;
    public BaseFragment(){}

    /*public static BaseFragment newInstance(BaseActivity activity){
        BaseFragment fr = new BaseFragment();
        fr.mActivity = activity;
        return fr;
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(getViewResourceId(), container, false);
        //findViews();
        //setInitialData();
        //attachEvents();
        //followingWorksAfterInflateView();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (BaseActivity)getActivity();
        followingWorksAfterInflateView();
        mActivity.changeColors();

    }

    /**
     * onCreateView()다음에 수행할 작업을 진행하도록 구현
     */
    protected void followingWorksAfterInflateView(){

    }
    /**
     * 뷰 구성요소를 추출 하도록 구현합니다.
     */
    protected void findViews(){
        throw new IllegalStateException("This method must be implemented by subclass.");
    }

    /**
     * 뷰 구성요소에 이벤트를 할당 하도록 구현합니다.
     */
    protected void attachEvents(){
        throw new IllegalStateException("This method must be implemented by subclass.");
    }

    protected TextView getTextView(int id){
        return (TextView)mRootView.findViewById(id);
    }

    protected EditText getEditText(int id){
        return (EditText)mRootView.findViewById(id);
    }
    protected ImageView getImageView(int id){
        return (ImageView)mRootView.findViewById(id);
    }

    protected CheckBox getCheckBox(int id){
        return (CheckBox)mRootView.findViewById(id);
    }
    protected String getText(TextView tv){
        return tv.getText().toString();
    }
    public void toast(int id){
        if(isAdded()) {
            toast(getString(id));
        }
    }
    public void toast(String txt){
        if(isAdded()) {
            mActivity.toast(txt);
        }
    }
    public void toastLong(int id){
        if(isAdded()) {
            toastLong(getString(id));
        }
    }
    public void toastLong(String txt){
        if(isAdded()) {
            mActivity.toastLong(txt);
        }
    }
    /**
     * 각 요소의 click event를 처리하도록 구현.
     * @param view
     */
    public void performClick(View view){
        throw new IllegalStateException("This method must be implemented by subclass.");
    }

    /**
     * 생성시 최초로 데이터를 처리하도록 구현.
     */
    protected void setInitialData(){
        throw new IllegalStateException("This method must be implemented by subclass.");
    }
    /**
     * 스피너 프로그레스 다이알로그를 노출합니다.
     * @param msg 메세지
     */
    public void showSpinner(String msg){
        if(isAdded() && mActivity != null) {
            mActivity.showSpinner(msg);
        }
    }

    /**
     * * 스피너 프로그레스 다이알로그를 숨깁니다.
     */
    public void closeSpinner(){
        if(isAdded() && mActivity != null) {
            mActivity.closeSpinner();
        }
    }

    /**
     * 새로운 데이터로 교체하도록 구현
     * @param params
     */
    public void setTargetData(Object...params){

    }

    /**
     * Each subclass return the own layout ID that make up their screens.
     * @return layout id.
     */
    protected int getLayoutId(){
        throw new IllegalStateException("This method body should be implemented by sub class.[getLayoutId()]");
    }

    /**
     * Returns the {@link View} on current screen.
     * @param viewResourceId view's id
     * @return View's sub class
     */
    public <T extends View> T getView(int viewResourceId){
        return mRootView.findViewById(viewResourceId);
    }
    /**
     * Fragment onCreateView()에서 사용할 layout id을 반환하도록 구현
     * @return
     */
    public int getViewResourceId(){
        return -1;
    }

    public void removeFrgament(){
        FragmentManager fm = mActivity.getSupportFragmentManager();
        if( fm != null ) {
            fm.beginTransaction().remove(this).commit();
        }
    }

    /**
     * Implement to handle event state when a click event occurs.
     * @param view
     */
    public void performFragmentClick(View view){
        throw new IllegalStateException("This method body should be implemented by sub class.[performClick()]");
    }


    /**
     * Implement to handle event state when a click event occurs.
     * @param view
     * @param position the view position occurred event
     */
    public void performFragmentClick(View view, int position){
        throw new IllegalStateException("This method body should be implemented by sub class.[performClick()]");
    }

    /**
     * 서버에 데이터를 요청할 수 있도록 구현
     */
    public void requestToServer(){
        throw new IllegalStateException("This method body should be implemented by sub class.[requestToServer()]");
    }
}
