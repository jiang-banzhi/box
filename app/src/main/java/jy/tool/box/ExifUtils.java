package jy.tool.box;

import android.media.ExifInterface;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/4/22.
 * @desciption :
 * @version :
 * </pre>
 */
public class ExifUtils {



    public static class ExifInfo {
        /**
         * 光圈值
         */
        private String FNumber;
        /**
         * 曝光时间
         */
        private String exposureTime;
        private String dateTime;
        private String SubsecTime;
        /**
         * 数字化时间
         */
        private String digitizedTime;
        /**
         * 拍摄时间
         */
        private String originalTime;
        /**
         * 闪光灯
         */
        private String flash;

        /**
         * 焦距
         */
        private String focalLength;
        /**
         * 图片长度
         */
        private String imageLength;
        /**
         * 图片宽度
         */
        private String imageWidth;
        /**
         * 感光度
         */
        private String iso;

        /**
         * 设备厂商
         */
        private String make;

        /**
         * 相机
         */
        private String model;
        /**
         * 维度
         */
        private String latitude;
        /**
         * 经度
         */
        private String longitude;
        /**
         * 海拔
         */
        private String alitude;


        public ExifInfo(ExifInterface exif) {
            if (exif == null) {
                return;
            }
            FNumber = exif.getAttribute(ExifInterface.TAG_APERTURE);
            dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
            exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            SubsecTime = exif.getAttribute(ExifInterface.TAG_SUBSEC_TIME);
            digitizedTime = exif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED);
            originalTime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
            flash = exif.getAttribute(ExifInterface.TAG_FLASH);
            focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            imageLength = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            imageWidth = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            iso = exif.getAttribute(ExifInterface.TAG_ISO);
            make = exif.getAttribute(ExifInterface.TAG_MAKE);
            model = exif.getAttribute(ExifInterface.TAG_MODEL);
            latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            alitude = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
        }
    }
}
