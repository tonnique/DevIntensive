package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.*;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.BoolRes;
import android.support.design.widget.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.Helper;
import com.softdesign.devintensive.utils.ValidationEditText;
import com.softdesign.devintensive.utils.DevTextWatcher;

import com.squareup.picasso.Picasso;

import java.io.*;
import java.util.*;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";

    private DataManager mDataManager;
    private int mCurrentEditMode = ConstantManager.EDIT_MODE_OFF;

    @BindView(R.id.phone_et)
    EditText mUserPhone;
    @BindView(R.id.email_et)
    EditText mUserMail;
    @BindView(R.id.vk_et)
    EditText mUserVk;
    @BindView(R.id.github_et)
    EditText mUserGit;
    @BindView(R.id.about_et)
    EditText mUserBio;

    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;
    @BindView(R.id.call_img)
    ImageView mCallImg;
    @BindView(R.id.send_img)
    ImageView mMailImg;
    @BindView(R.id.vk_img)
    ImageView mVkImg;
    @BindView(R.id.git_img)
    ImageView mGitImg;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    @BindViews({R.id.phone_et, R.id.email_et, R.id.vk_et, R.id.github_et, R.id.about_et})
    List<EditText> mUserInfoViews;

    @BindView(R.id.user_rating_txt) TextView mUserValueRating;
    @BindView(R.id.user_codelines_txt) TextView mUserValueCodeLines;
    @BindView(R.id.user_projects_txt) TextView mUserValueProjects;

    @BindViews({R.id.user_rating_txt, R.id.user_codelines_txt, R.id.user_projects_txt})
    List<TextView> mUserValueViews;

    /**
     * вызывается при создании или перезапуске активности
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        mUserPhone.addTextChangedListener(new DevTextWatcher(mUserPhone));
        mUserMail.addTextChangedListener(new DevTextWatcher(mUserMail));
        mUserVk.addTextChangedListener(new DevTextWatcher(mUserVk));
        mUserGit.addTextChangedListener(new DevTextWatcher(mUserGit));

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        setupToolbar();

        loadUserInfoValues();

        setupDrawer();

        initUserInfoValues();
        initProfileImage();


        if (savedInstanceState == null) {
            // активность запускается впервые
            //setUserProfileDummyValues();
        } else {
            // активность уже создавалась
            mCurrentEditMode = savedInstanceState
                    .getInt(ConstantManager.EDIT_MODE_KEY, ConstantManager.EDIT_MODE_OFF);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Вызывается непосредственно перед тем, как активность становится видимой пользователю
     * Чтение из базы данных
     * Запуск сложной анимации
     * Запуск потоков, отслеживания показаний датчиков, запросов к GPS, таймеров, сервисов или других процессов,
     * которые нужны исключительно для обновления пользовательского интерфейса
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    /**
     * Методon Resume()вызывается послеonStart().Также может вызываться послеonPause().
     * запуск воспроизведения анимации, аудио и видео
     * инициализации компонентов
     * регистрации любых широковещательных приемников или других процессов, которые вы освободили/приостановили вonPause()
     * выполнение любых другие инициализации, которые должны происходить, когда активность вновь активна (камера).
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * МетодonPause()вызывается после сворачивания текущей активности или перехода к новому.
     * От onPause()можно перейти к вызову либо onResume(), либо onStop().
     * <p>
     * остановка анимации, аудио и видео
     * фиксация несохраненных данных (легкие процессы)
     * освобождение системных ресурсов
     * остановка сервисов, подписок, широковещательных сообщений
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        /*
        if (validateUserInfoValues(false)) {
            saveUserInfoValues();
        } */
    }

    /**
     * МетодonStop()вызывается, когда окно становится невидимым для пользователя.
     * Это может произойти при её уничтожении, или если была запущена другая активность (существующая или новая),
     * перекрывшая окно текущей активности.
     * <p>
     * запись в базу данных
     * приостановка сложной анимации
     * приостановка потоков, отслеживания показаний датчиков, запросов к GPS, таймеров,
     * сервисов или других процессов, которые нужны исключительно для обновления пользовательского интерфейса
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /**
     * Метод вызывается по окончании работы активности, при вызове методаfinish()или в случае,
     * когда система уничтожает этот экземпляр активности для освобождения ресурсов.
     * <p>
     * высвобождение ресурсов
     * дополнительная перестраховка если ресурсы не были выгружены или процессы не
     * были остановлены ранее
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /**
     * Если окно возвращается в приоритетный режим после вызоваonStop(), то в этом случае вызывается методonRestart().
     * Т.е. вызывается после того, как активность была остановлена и снова была запущена пользователем.
     * Всегда сопровождается вызовом метода onStart().
     * <p>
     * используется для специальных действий, которые должны выполняться только при повторном запуске активности
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    static final ButterKnife.Setter<View, Boolean> Enablead = new ButterKnife.Setter<View, Boolean>() {
        @Override
        public void set(View view, Boolean value, int index) {
            view.setEnabled(value);
            view.setFocusable(value);
            view.setFocusableInTouchMode(value);
        }
    };

    /**
     * Реализация "прослушивателя событий" с помощью Butterknife для некоторых ImageView
     *
     * @param imageView
     */
    @OnClick({R.id.call_img, R.id.send_img, R.id.vk_img, R.id.git_img})
    public void imageClick(ImageView imageView) {
        //Если в режиме просмотра, то выполняем дейтвия
        if (mCurrentEditMode == ConstantManager.EDIT_MODE_OFF) {
            switch (imageView.getId()) {
                case R.id.call_img:
                    dialToPhone(mUserPhone.getText().toString().trim());
                    break;
                case R.id.send_img:
                    sendMail(mUserMail.getText().toString().trim());
                    break;
                case R.id.vk_img:
                    browseUrl(mUserVk.getText().toString().trim());
                    break;
                case R.id.git_img:
                    browseUrl(mUserGit.getText().toString().trim());
                    break;
            }
        }
    }

    /**
     * Обработка нажатий на элементы экрана
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == ConstantManager.EDIT_MODE_OFF) {
                    changeEditMode(ConstantManager.EDIT_MODE_ON);
                    mCurrentEditMode = ConstantManager.EDIT_MODE_ON;
                } else {
                    changeEditMode(ConstantManager.EDIT_MODE_OFF);
                    mCurrentEditMode = ConstantManager.EDIT_MODE_OFF;
                }
                break;
            case R.id.profile_placeholder:
                //Делаем выбор откуда загружать фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
        }
    }

    /**
     * Сохранение текущего состояния
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    /**
     * Выполняет инициализацию панели Toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Выполняет инициализацию фотографии в профиле пользователя.
     * Загружает фотографию с сервера. В случае неудачи использует локальное изображение.
     */
    private void initProfileImage() {
        String photoURL = getIntent().getStringExtra(ConstantManager.USER_PHOTO_URL_KEY);
        final Uri photoLocalUri = mDataManager.getPreferencesManager().loadUserPhoto();

        Picasso.with(MainActivity.this)
                .load(photoLocalUri)
                .placeholder(R.drawable.user_bg)
                .into(mProfileImage);

        Call<ResponseBody> call = mDataManager.getImage(photoURL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    if (bitmap != null) {
                        mProfileImage.setImageBitmap(bitmap);
                        try {
                            File file = createImageFileFromBitmap("user_photo", bitmap);
                            if (file != null) {
                                mDataManager.getPreferencesManager()
                                        .saveUserPhoto(Uri.fromFile(file));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Helper.showSnackbar(mCoordinatorLayout, "Не удалось загрузить фотографию пользователя");
            }
        });
    }

    /**
     * Загружает фотографию из профиля пользователя на сервер
     * @param photoFile представление файла фотографии
     */
    private void uploadPhoto(File photoFile) {
        Call<ResponseBody> call = mDataManager.uploadPhoto(photoFile);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("TAG", "Upload success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Helper.showSnackbar(mCoordinatorLayout, "Не удалось загрузить фотографию на сервер");
                //Log.e("Upload error", t.getMessage());
            }
        });
    }

    /**
     * Выполняет инициализацию выдвижной панели навигационного меню
     */
    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        Helper.setupHeaderDrawerImage(this, navigationView, BitmapFactory.decodeResource(getResources(), R.drawable.avatar));

        View headerView = navigationView.getHeaderView(0);

        List<String> userNames = mDataManager.getPreferencesManager().loadUserName();
        ((TextView)headerView.findViewById(R.id.user_name_txt))
                .setText(String.format("%s %s", userNames.get(1), userNames.get(0)));
        ((TextView)headerView.findViewById(R.id.user_email_txt))
                .setText(mUserMail.getText());

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        Helper.showSnackbar(mCoordinatorLayout, item.getTitle().toString());
                        item.setChecked(true);
                        mNavigationDrawer.closeDrawer(GravityCompat.START);
                        return false;
                    }
                });
    }

    /**
     * Получение результата из другой Activity (фото из камеры или галлереи)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }

    /**
     * Переключает режим редактирования
     *
     * @param mode если 1 - режим редактирования, если 0 - режим просмотра
     */
    private void changeEditMode(int mode) {
        if (mode == ConstantManager.EDIT_MODE_ON) {
            mFab.setImageResource(R.drawable.ic_done_white_24dp);
            /*
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            } */
            ButterKnife.apply(mUserInfoViews, Enablead, true);
            /*Устанавливаем фокус на поле с номером телефона и показываем клавиатуру*/
            mUserPhone.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            //setFieldFocus(mUserPhone, false);

            showProfilePlaceholder();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

        } else {
            //Проверяем введенные данные на валидность
            if (checkValidationEditText()) {
                mFab.setImageResource(R.drawable.ic_create_white_24dp);

                ButterKnife.apply(mUserInfoViews, Enablead, false);
                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

                saveUserInfoValues();
            } else {
                showToast(getString(R.string.validation_edit_mode_error));
            }

            /*
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }

            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

            saveUserInfoValues();
            */
        }
    }

    /**
     * Проверка правильности заполнения полей
     *
     * @return
     */
    private boolean checkValidationEditText() {
        boolean ret = true;
        //Мобильный телефн
        if (!ValidationEditText.isPhoneNumber(mUserPhone, false)) {
            mUserPhone.requestFocus();
            ret = false;
        }
        //EMail
        if (!ValidationEditText.isEmailAddress(mUserMail, false)) {
            mUserMail.requestFocus();
            ret = false;
        }
        //Профиль VK
        if (!ValidationEditText.isVkProfile(mUserVk, false)) {
            mUserVk.requestFocus();
            ret = false;
        }
        //Профиль Github
        if (!ValidationEditText.isGitProfile(mUserGit, false)) {
            mUserGit.requestFocus();
            ret = false;
        }
        return ret;
    }

    /**
     * Загружает пользовательские данные
     */
    private void loadUserInfoValues() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохраняет пользовательские данные
     */
    private void saveUserInfoValues() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * обрабатывем нажатие кнопки Back
     */
    @Override
    public void onBackPressed() {
        if (mNavigationDrawer != null && mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Загружает данные пользователя (рейтинг, строки кода, проекта) из Shared Preferences
     * в текстовые поля заголовка профиля
     */
    private void initUserInfoValues() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }


    /**
     * Загружаем фотографию из галлереи
     */
    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        try {
            startActivityForResult(Intent.createChooser(takeGalleryIntent,
                    getString(R.string.user_profile_chose_message)),
                    ConstantManager.REQUEST_GALLERY_PICTURE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(getString(R.string.load_photo_gallery_exception));
        }
    }

    /**
     * Запускает получение фотографии из камеры
     */
    private void loadPhotoFromCamera() {
        // проверка разрешений для Android 6
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                mPhotoFile = Helper.createImageFile(this);
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error(getString(R.string.load_photo_camera_exception));
            }
            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                try {
                    startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Error(getString(R.string.load_photo_camera_exception_activityStart));
                }
            }
        } else {
            // запрос разрешений на использование камеры и внешнего хранилища данных
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            // предоставление пользователю возможности установить разрешения,
            // если он ранее запретил их и выбрал опцию "не показывать больше"
            Snackbar.make(mCoordinatorLayout, R.string.snackbar_msg_permissions_request, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_allow_permissions, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    /**
     * Получение разрешений на работу с камерой и сохранение изображений
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE
                && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: тут обрабатываем разрешение (разрешение получено)
                loadPhotoFromCamera();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // TODO: тут обрабатываем разрешение (разрешение получено)
                loadPhotoFromGallery();
            }
        }
    }

    /**
     * Отображает плейсхолдер фотографии в профайле пользователя
     */
    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     * Скрывает плейсхолдер фотографии в профайле пользователя
     */
    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * Блокирует сворачивание Collapsing Toolbar
     */
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        // сбрасывает флаги скроллинга
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Разблокирует сворачивание Collapsing Toolbar
     */
    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Диалог загрузки фото из камеры или из галлереи
     * @param id
     * @return
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {
                        getString(R.string.user_profile_dialog_gallery),
                        getString(R.string.user_profile_dialog_camera),
                        getString(R.string.user_profile_dialog_cancel)
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                //Загрузить из галлереи
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                //Загрузить из камеры
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                //Отменить
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }

    /**
     * Создает файл с для хранения изображения во внешнем хранилище
     * @param imageFileName имя файла
     * @param bitmap растровое изображение для сохранения в файле
     * @return представление созданного файла изображения
     * @throws IOException
     */
    private File createImageFileFromBitmap(String imageFileName, Bitmap bitmap) throws IOException {

        // проверка разрешений для Android 6
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File imageFile = null;
            try {
                imageFile = new File(storageDir, imageFileName + ".jpg");

                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Android 6 ???: сохранение изображения с добавлением в галлерею
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, imageFile.getAbsolutePath());

            this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            return imageFile;
        } else {
            // запрос разрешений на использование камеры и внешнего хранилища данных
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            // предоставление пользователю возможности установить разрешения,
            // если он ранее запретил их и выбрал опцию "не показывать больше"
            Snackbar.make(mCoordinatorLayout, R.string.snackbar_msg_permissions_request, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_allow_permissions, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();

            return null;
        }

    }

    /**
     * Вставляем выбранное фото в профайл с заданным URI, сохраняет URI в Shared Preferences
     * @param selectedImage URI изображения
     */
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .placeholder(R.drawable.profile_image)
                .into(mProfileImage);

        // если URI фотографии изменился
        if (!selectedImage.equals(mDataManager.getPreferencesManager().loadUserPhoto())) {

            if (selectedImage.getLastPathSegment().endsWith(".jpg")) {
                uploadPhoto(new File(selectedImage.getPath()));
            } else {
                uploadPhoto(new File(getPath(selectedImage)));
            }

            mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
        }

        //mDataManager.getFileUploader().uploadFile(selectedImage);

    }

    /**
     * Возвращает путь к файлу для заданного URI
     * (используется для корректной выгрузки на сервер при выборе файла в галерее)
     * @param uri URI файла
     * @return путь к файлу
     */
    //@Nullable
    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    /**
     * Открывает настройки приложения
     */
    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.REQUEST_SETTINGS_CODE);
    }


    /**
     * Инициирует телефонный звонок на заданный номер
     * @param phoneStr номер телефона
     */
    private void dialToPhone(String phoneStr) {

        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneStr));

        //Проверка, что существует приложение, способное обработать Intent
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            //Проверяем разрешение на звонок
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CALL_PHONE
                }, ConstantManager.PHONE_REQUEST_PERMISSION_CODE);

                Snackbar.make(mCoordinatorLayout,
                        R.string.snackbar_msg_permissions_request,
                        Snackbar.LENGTH_LONG)
                        .setAction(R.string.action_allow_permissions, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openApplicationSettings();
                            }
                        }).show();
                return;
            }
        } else {
            showToast("Нет приложения для вызова телефона");
            return;
        }
        try {
            startActivity(dialIntent);
        } catch (Exception e){
            e.printStackTrace();
            //throw new Error("Не удалось сделать вызов!");
        }
    }


    /**
     * Инициирует отправку письма по электронной почте
     * @param email адрес электронной почты
     */
    private void sendMail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        //Кому
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { email});
        // Тема письма
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Тема письма");
        // Текст письма
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Текст письма");
        //проверяем, есть ли приложения, отправляющие почту
        if (emailIntent.resolveActivity(getPackageManager()) == null){
            showToast("Нет приложения для отправки e-mail");
            return;
        }
        try {
            startActivity(Intent.createChooser(emailIntent, "Отправка письма на e-mail"));
        } catch (Exception e){
            e.printStackTrace();
            //throw new Error("Ошибка отправки сообщения!");
        }
    }

    /**
     * Открывает ссылку в браузере по заданному URL-адресу
     * @param url URL-адрес
     */
    private void browseUrl(String url) {
        //url = "https://" + url;
        url = url;

        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(browseIntent);
        } catch (Exception e) {
            e.printStackTrace();
            //throw new Error(getString(R.string.browsActivity_error));
        }
    }
}
