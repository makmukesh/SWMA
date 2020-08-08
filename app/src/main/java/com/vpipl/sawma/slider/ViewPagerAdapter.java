package com.vpipl.sawma.slider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.vpipl.sawma.R;
import com.vpipl.sawma.Utils.AppUtils;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<SliderUtils> sliderImg;


    public ViewPagerAdapter(List sliderImg, Context context) {
        this.sliderImg = sliderImg;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderImg.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.slider_layout, null);
        SliderUtils utils = sliderImg.get(position);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        try
        {
            AppUtils.loadSlidingImage(context ,  "" + utils.getSliderImageUrl() , imageView);
         //   imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
            //imageLoader.get(utils.getSliderImageUrl(), ImageLoader.getImageListener(imageView, R.drawable.dummy1, R.drawable.dummy1));


        }catch(Exception ex)
        {
            ex.printStackTrace();
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String imageurl = sliderImg.get(position).getSliderImageUrl();

               /* new PhotoFullPopupWindow(context, R.layout.image_preview, view, imageurl, null);

                Intent intent = new Intent(context, ZoomTest.class);
                intent.putExtra("image",imageurl);
                context.startActivity(intent);*/
            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}