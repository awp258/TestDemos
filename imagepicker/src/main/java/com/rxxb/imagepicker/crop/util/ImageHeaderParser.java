//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rxxb.imagepicker.crop.util;

import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class ImageHeaderParser {
    private static final String TAG = "ImageHeaderParser";
    public static final int UNKNOWN_ORIENTATION = -1;
    private static final int EXIF_MAGIC_NUMBER = 65496;
    private static final int MOTOROLA_TIFF_MAGIC_NUMBER = 19789;
    private static final int INTEL_TIFF_MAGIC_NUMBER = 18761;
    private static final String JPEG_EXIF_SEGMENT_PREAMBLE = "Exif\u0000\u0000";
    private static final byte[] JPEG_EXIF_SEGMENT_PREAMBLE_BYTES = "Exif\u0000\u0000".getBytes(Charset.forName("UTF-8"));
    private static final int SEGMENT_SOS = 218;
    private static final int MARKER_EOI = 217;
    private static final int SEGMENT_START_ID = 255;
    private static final int EXIF_SEGMENT_TYPE = 225;
    private static final int ORIENTATION_TAG_TYPE = 274;
    private static final int[] BYTES_PER_FORMAT = new int[]{0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};
    private final ImageHeaderParser.Reader reader;

    public ImageHeaderParser(InputStream is) {
        this.reader = new ImageHeaderParser.StreamReader(is);
    }

    public int getOrientation() throws IOException {
        int magicNumber = this.reader.getUInt16();
        if (!handles(magicNumber)) {
            if (Log.isLoggable("ImageHeaderParser", 3)) {
                Log.d("ImageHeaderParser", "Parser doesn't handle magic number: " + magicNumber);
            }

            return -1;
        } else {
            int exifSegmentLength = this.moveToExifSegmentAndGetLength();
            if (exifSegmentLength == -1) {
                if (Log.isLoggable("ImageHeaderParser", 3)) {
                    Log.d("ImageHeaderParser", "Failed to parse exif segment length, or exif segment not found");
                }

                return -1;
            } else {
                byte[] exifData = new byte[exifSegmentLength];
                return this.parseExifSegment(exifData, exifSegmentLength);
            }
        }
    }

    private int parseExifSegment(byte[] tempArray, int exifSegmentLength) throws IOException {
        int read = this.reader.read(tempArray, exifSegmentLength);
        if (read != exifSegmentLength) {
            if (Log.isLoggable("ImageHeaderParser", 3)) {
                Log.d("ImageHeaderParser", "Unable to read exif segment data, length: " + exifSegmentLength + ", actually read: " + read);
            }

            return -1;
        } else {
            boolean hasJpegExifPreamble = this.hasJpegExifPreamble(tempArray, exifSegmentLength);
            if (hasJpegExifPreamble) {
                return parseExifSegment(new ImageHeaderParser.RandomAccessReader(tempArray, exifSegmentLength));
            } else {
                if (Log.isLoggable("ImageHeaderParser", 3)) {
                    Log.d("ImageHeaderParser", "Missing jpeg exif preamble");
                }

                return -1;
            }
        }
    }

    private boolean hasJpegExifPreamble(byte[] exifData, int exifSegmentLength) {
        boolean result = exifData != null && exifSegmentLength > JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length;
        if (result) {
            for(int i = 0; i < JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length; ++i) {
                if (exifData[i] != JPEG_EXIF_SEGMENT_PREAMBLE_BYTES[i]) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    private int moveToExifSegmentAndGetLength() throws IOException {
        while(true) {
            short segmentId = this.reader.getUInt8();
            if (segmentId != 255) {
                if (Log.isLoggable("ImageHeaderParser", 3)) {
                    Log.d("ImageHeaderParser", "Unknown segmentId=" + segmentId);
                }

                return -1;
            }

            short segmentType = this.reader.getUInt8();
            if (segmentType == 218) {
                return -1;
            }

            if (segmentType == 217) {
                if (Log.isLoggable("ImageHeaderParser", 3)) {
                    Log.d("ImageHeaderParser", "Found MARKER_EOI in exif segment");
                }

                return -1;
            }

            int segmentLength = this.reader.getUInt16() - 2;
            if (segmentType != 225) {
                long skipped = this.reader.skip((long)segmentLength);
                if (skipped == (long)segmentLength) {
                    continue;
                }

                if (Log.isLoggable("ImageHeaderParser", 3)) {
                    Log.d("ImageHeaderParser", "Unable to skip enough data, type: " + segmentType + ", wanted to skip: " + segmentLength + ", but actually skipped: " + skipped);
                }

                return -1;
            }

            return segmentLength;
        }
    }

    private static int parseExifSegment(ImageHeaderParser.RandomAccessReader segmentData) {
        int headerOffsetSize = "Exif\u0000\u0000".length();
        short byteOrderIdentifier = segmentData.getInt16(headerOffsetSize);
        ByteOrder byteOrder;
        if (byteOrderIdentifier == 19789) {
            byteOrder = ByteOrder.BIG_ENDIAN;
        } else if (byteOrderIdentifier == 18761) {
            byteOrder = ByteOrder.LITTLE_ENDIAN;
        } else {
            if (Log.isLoggable("ImageHeaderParser", 3)) {
                Log.d("ImageHeaderParser", "Unknown endianness = " + byteOrderIdentifier);
            }

            byteOrder = ByteOrder.BIG_ENDIAN;
        }

        segmentData.order(byteOrder);
        int firstIfdOffset = segmentData.getInt32(headerOffsetSize + 4) + headerOffsetSize;
        int tagCount = segmentData.getInt16(firstIfdOffset);

        for(int i = 0; i < tagCount; ++i) {
            int tagOffset = calcTagOffset(firstIfdOffset, i);
            int tagType = segmentData.getInt16(tagOffset);
            if (tagType == 274) {
                int formatCode = segmentData.getInt16(tagOffset + 2);
                if (formatCode >= 1 && formatCode <= 12) {
                    int componentCount = segmentData.getInt32(tagOffset + 4);
                    if (componentCount < 0) {
                        if (Log.isLoggable("ImageHeaderParser", 3)) {
                            Log.d("ImageHeaderParser", "Negative tiff component count");
                        }
                    } else {
                        if (Log.isLoggable("ImageHeaderParser", 3)) {
                            Log.d("ImageHeaderParser", "Got tagIndex=" + i + " tagType=" + tagType + " formatCode=" + formatCode + " componentCount=" + componentCount);
                        }

                        int byteCount = componentCount + BYTES_PER_FORMAT[formatCode];
                        if (byteCount > 4) {
                            if (Log.isLoggable("ImageHeaderParser", 3)) {
                                Log.d("ImageHeaderParser", "Got byte count > 4, not orientation, continuing, formatCode=" + formatCode);
                            }
                        } else {
                            int tagValueOffset = tagOffset + 8;
                            if (tagValueOffset >= 0 && tagValueOffset <= segmentData.length()) {
                                if (byteCount >= 0 && tagValueOffset + byteCount <= segmentData.length()) {
                                    return segmentData.getInt16(tagValueOffset);
                                }

                                if (Log.isLoggable("ImageHeaderParser", 3)) {
                                    Log.d("ImageHeaderParser", "Illegal number of bytes for TI tag data tagType=" + tagType);
                                }
                            } else if (Log.isLoggable("ImageHeaderParser", 3)) {
                                Log.d("ImageHeaderParser", "Illegal tagValueOffset=" + tagValueOffset + " tagType=" + tagType);
                            }
                        }
                    }
                } else if (Log.isLoggable("ImageHeaderParser", 3)) {
                    Log.d("ImageHeaderParser", "Got invalid format code = " + formatCode);
                }
            }
        }

        return -1;
    }

    private static int calcTagOffset(int ifdOffset, int tagIndex) {
        return ifdOffset + 2 + 12 * tagIndex;
    }

    private static boolean handles(int imageMagicNumber) {
        return (imageMagicNumber & '\uffd8') == 65496 || imageMagicNumber == 19789 || imageMagicNumber == 18761;
    }

    public static void copyExif(ExifInterface originalExif, int width, int height, String imageOutputPath) {
        String[] attributes = new String[]{"FNumber", "DateTime", "DateTimeDigitized", "ExposureTime", "Flash", "FocalLength", "GPSAltitude", "GPSAltitudeRef", "GPSDateStamp", "GPSLatitude", "GPSLatitudeRef", "GPSLongitude", "GPSLongitudeRef", "GPSProcessingMethod", "GPSTimeStamp", "ISOSpeedRatings", "Make", "Model", "SubSecTime", "SubSecTimeDigitized", "SubSecTimeOriginal", "WhiteBalance"};

        try {
            ExifInterface newExif = new ExifInterface(imageOutputPath);
            String[] var7 = attributes;
            int var8 = attributes.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String attribute = var7[var9];
                String value = originalExif.getAttribute(attribute);
                if (!TextUtils.isEmpty(value)) {
                    newExif.setAttribute(attribute, value);
                }
            }

            newExif.setAttribute("ImageWidth", String.valueOf(width));
            newExif.setAttribute("ImageLength", String.valueOf(height));
            newExif.setAttribute("Orientation", "0");
            newExif.saveAttributes();
        } catch (IOException var11) {
            Log.d("ImageHeaderParser", var11.getMessage());
        }

    }

    private static class StreamReader implements ImageHeaderParser.Reader {
        private final InputStream is;

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public int getUInt16() throws IOException {
            return this.is.read() << 8 & '\uff00' | this.is.read() & 255;
        }

        public short getUInt8() throws IOException {
            return (short)(this.is.read() & 255);
        }

        public long skip(long total) throws IOException {
            if (total < 0L) {
                return 0L;
            } else {
                long toSkip = total;

                while(toSkip > 0L) {
                    long skipped = this.is.skip(toSkip);
                    if (skipped > 0L) {
                        toSkip -= skipped;
                    } else {
                        int testEofByte = this.is.read();
                        if (testEofByte == -1) {
                            break;
                        }

                        --toSkip;
                    }
                }

                return total - toSkip;
            }
        }

        public int read(byte[] buffer, int byteCount) throws IOException {
            int toRead;
            int read;
            for(toRead = byteCount; toRead > 0 && (read = this.is.read(buffer, byteCount - toRead, toRead)) != -1; toRead -= read) {
            }

            return byteCount - toRead;
        }
    }

    private interface Reader {
        int getUInt16() throws IOException;

        short getUInt8() throws IOException;

        long skip(long var1) throws IOException;

        int read(byte[] var1, int var2) throws IOException;
    }

    private static class RandomAccessReader {
        private final ByteBuffer data;

        public RandomAccessReader(byte[] data, int length) {
            this.data = (ByteBuffer)ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).limit(length);
        }

        public void order(ByteOrder byteOrder) {
            this.data.order(byteOrder);
        }

        public int length() {
            return this.data.remaining();
        }

        public int getInt32(int offset) {
            return this.data.getInt(offset);
        }

        public short getInt16(int offset) {
            return this.data.getShort(offset);
        }
    }
}
