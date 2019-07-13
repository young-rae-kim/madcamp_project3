package com.example.libraryapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private DatabaseReference ref;
    private final int SEND_PICTURE = 0;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int BARCODE = 2;
    private int cartSize = 0;
    private boolean imageFilled = false;
    private CardView cartCard;
    private CardView barcodeCard;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private ImageView uploadView;
    private ImageView cameraView;
    private TextView textView;
    private String currentPhotoPath;
    private TextInputEditText editTitle;
    private TextInputEditText editAuthor;
    private TextInputEditText editPublisher;
    private MaterialButton addCart;
    private MaterialButton addLibrary;
    private ArrayList<BookItem> cartItems = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private CartAdapter mCartAdapter;
    private BottomSheetBehavior bottomSheetBehavior;
    private RandomISBN randomISBN = new RandomISBN();

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("server/saving-data/");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_fragment, container, false);
        barcodeCard = view.findViewById(R.id.barcodeCard);
        cartCard = view.findViewById(R.id.cartCard);
        imageView = view.findViewById(R.id.thumbnailImage);
        textView = view.findViewById(R.id.thumbnailImageText);
        editTitle = view.findViewById(R.id.editTitle);
        editAuthor = view.findViewById(R.id.editAuthor);
        editPublisher = view.findViewById(R.id.editPublisher);
        addCart = view.findViewById(R.id.addCart);
        addLibrary = view.findViewById(R.id.addLibrary);
        recyclerView = view.findViewById(R.id.recycler_cart);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mCartAdapter = new CartAdapter(cartItems, Glide.with(this));
        recyclerView.setAdapter(mCartAdapter);

        LinearLayout llBottomSheet = view.findViewById(R.id.bottom_menu);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        uploadView = view.findViewById(R.id.uploadIcon);
        cameraView = view.findViewById(R.id.cameraIcon);

        uploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture to Upload"), SEND_PICTURE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.e("IMAGE_CAPTURE", ex.getMessage());
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getContext(),
                                "com.example.android.fileprovider",
                                photoFile);
                        if (photoURI != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        if (cartSize == 0) {
            cartCard.setVisibility(View.GONE);
        }
        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        barcodeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BarcodeActivity.class);
                startActivityForResult(intent, BARCODE);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageFilled && currentPhotoPath != null) {
                    File imgFile = new File(currentPhotoPath);
                    if (imgFile.exists()) {
                        if (!imgFile.delete()) { return; } else { currentPhotoPath = null; }
                    }
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(currentPhotoPath)) {
                    Toast.makeText(getContext(), "Thumbnail cannot be null or empty.", Toast.LENGTH_LONG)
                            .show();
                    return;
                } else if (TextUtils.isEmpty(editTitle.getText().toString())) {
                    Toast.makeText(getContext(), "Title cannot be null or empty.", Toast.LENGTH_LONG)
                            .show();
                    return;
                } else if (TextUtils.isEmpty(editAuthor.getText().toString())) {
                    Toast.makeText(getContext(), "Author cannot be null or empty.", Toast.LENGTH_LONG)
                            .show();
                    return;
                } else if (TextUtils.isEmpty(editPublisher.getText().toString())) {
                    Toast.makeText(getContext(), "Publisher cannot be null or empty.", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                BookItem newItem = new BookItem(currentPhotoPath,
                            editTitle.getText().toString(),
                            editAuthor.getText().toString(),
                            editPublisher.getText().toString(),
                            "Undetermined",
                            randomISBN.GeneratingRandomStringBounded());

                mCartAdapter.getItems().add(newItem);
                mCartAdapter.notifyItemInserted(mCartAdapter.getItemCount() - 1);
                cartSize = mCartAdapter.getItemCount();
                editAuthor.getText().clear();
                editPublisher.getText().clear();
                editTitle.getText().clear();
                cartCard.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                imageFilled = false;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mCartAdapter.getItems().remove(position);
                        mCartAdapter.notifyItemRemoved(position);
                        cartSize = mCartAdapter.getItemCount();
                        if (cartSize == 0)
                            cartCard.setVisibility(View.GONE);
                    }
                }));

        addLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
        }
        return view;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        String path = Environment.getExternalStorageDirectory().toString();
        File storageDir = new File(path);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEND_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        String path = getRealPathFromURI(getContext(), selectedImageUri);
                        currentPhotoPath = path;
                        Glide.with(this)
                                .load(path)
                                .thumbnail(0.25f)
                                .into(imageView);
                        textView.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        imageFilled = true;
                    } else {
                        imageView.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }
                } else {
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File f = new File(currentPhotoPath);
                    Uri contentUri = Uri.fromFile(f);
                    intent.setData(contentUri);
                    getActivity().sendBroadcast(intent);
                    Glide.with(this)
                            .load(currentPhotoPath)
                            .thumbnail(0.25f)
                            .into(imageView);
                    textView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    imageFilled = true;
                } else {
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
                break;
            case BARCODE:
                if (resultCode == Activity.RESULT_OK) {
                    BookItem newItem = new BookItem(data.getStringExtra("thumbnail"),
                            data.getStringExtra("title"),
                            data.getStringExtra("author"),
                            data.getStringExtra("publisher"),
                            data.getStringExtra("pubdate"),
                            data.getStringExtra("isbn"));

                    mCartAdapter.getItems().add(newItem);
                    mCartAdapter.notifyItemInserted(mCartAdapter.getItemCount() - 1);
                    cartSize = mCartAdapter.getItemCount();
                    editAuthor.getText().clear();
                    editPublisher.getText().clear();
                    editTitle.getText().clear();
                    cartCard.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    imageFilled = false;

                    Log.e("Search Book", mCartAdapter.getItems().get(mCartAdapter.getItemCount() - 1).getThumbnail() + ", "
                            + mCartAdapter.getItems().get(mCartAdapter.getItemCount() - 1).getTitle() + ", "
                            + mCartAdapter.getItems().get(mCartAdapter.getItemCount() - 1).getAuthor() + ", "
                            + mCartAdapter.getItems().get(mCartAdapter.getItemCount() - 1).getPublisher() + ", "
                            + mCartAdapter.getItems().get(mCartAdapter.getItemCount() - 1).getPubdate());
                }
                break;
            default:
                Log.e("BARCODE", "onResult");
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    public static String getRealPathFromURI(final Context context, final Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                } else {
                    String SDcardpath = getRemovableSDCardPath(context).split("/Android")[0];
                    return SDcardpath +"/"+ split[1];
                }
            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public static String getRemovableSDCardPath(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return storages[1].toString();
        else
            return "";
    }


    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public void searchBook(final String ISBN) {
        final String clientId = "ANdtuRVWpm_4fbzP2T_V";
        final String clientSecret = "8KJe8lEitY";
        final int display = 1;

        new Thread() {
            @Override
            public void run() {
                try {
                    String apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_isbn=" + ISBN + "&display=" + display + "&"; // json 결과
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    con.connect();

                    BufferedReader br;
                    int responseCode = con.getResponseCode();
                    if(responseCode == 200) {
                        InputStream inputStream = con.getInputStream();
                        BarcodeXmlParser parser = new BarcodeXmlParser();
                        br = new BufferedReader(new InputStreamReader(inputStream));
                        ArrayList<BookItem> resultList = parser.parse(inputStream);
                        br.close();
                        con.disconnect();
                        return;
                    } else {
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                        StringBuilder searchResult = new StringBuilder();
                        String inputLine;
                        while ((inputLine = br.readLine()) != null) {
                            searchResult.append(inputLine + "\n");
                        }
                        Log.e("Search Book", "Error : " + responseCode + ", " + searchResult.toString());
                        br.close();
                        con.disconnect();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
