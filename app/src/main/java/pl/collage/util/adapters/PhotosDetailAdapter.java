package pl.collage.util.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import pl.collage.gallerydetail.GalleryDetailFragment;
import pl.collage.util.models.Photo;

import java.util.List;

public class PhotosDetailAdapter extends FragmentStatePagerAdapter {

    private List<Photo> photoList;
    public static final String EXTRAS_PHOTO_URL = "photoUrl";
    public static final String EXTRAS_CACHED_BITMAP = "cachedBitmap";

    public PhotosDetailAdapter(FragmentManager fm, List<Photo> photoList) {
        super(fm);
        this.photoList = photoList;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRAS_PHOTO_URL, photoList.get(position).imageUrl);
        arguments.putParcelable(EXTRAS_CACHED_BITMAP, PhotosAdapter.cachedPhotoArray.get(position));

        GalleryDetailFragment galleryDetailFragment = new GalleryDetailFragment();
        galleryDetailFragment.setArguments(arguments);

        return galleryDetailFragment;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }
}
