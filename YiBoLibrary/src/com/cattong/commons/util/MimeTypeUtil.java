package com.cattong.commons.util;

import java.util.HashMap;

public class MimeTypeUtil {
	/**
     * Singleton MIME-type map instance:
     */
    private static MimeTypeUtil mimeTypeUtil;

    /**
     * MIME-type to file extension mapping:
     */
    private HashMap<String, String> mMimeTypeToExtensionMap;

    /**
     * File extension to MIME type mapping:
     */
    private HashMap<String, String> mExtensionToMimeTypeMap;

    /**
     * Creates a new MIME-type map.
     */
    private MimeTypeUtil() {
        mMimeTypeToExtensionMap = new HashMap<String, String>();
        mExtensionToMimeTypeMap = new HashMap<String, String>();
    }

    /**
     * Load an entry into the map. This does not check if the item already
     * exists, it trusts the caller!
     */
    private void loadEntry(String mimeType, String extension) {
        //
        // if we have an existing x --> y mapping, we do not want to
        // override it with another mapping x --> ?
        // this is mostly because of the way the mime-type map below
        // is constructed (if a mime type maps to several extensions
        // the first extension is considered the most popular and is
        // added first; we do not want to overwrite it later).
        //
        if (!mMimeTypeToExtensionMap.containsKey(mimeType)) {
            mMimeTypeToExtensionMap.put(mimeType, extension);
        }

        mExtensionToMimeTypeMap.put(extension, mimeType);
    }

    /**
     * Return true if the given MIME type has an entry in the map.
     * @param mimeType A MIME type (i.e. text/plain)
     * @return True iff there is a mimeType entry in the map.
     */
    public boolean hasMimeType(String mimeType) {
        if (mimeType != null && mimeType.length() > 0) {
            return mMimeTypeToExtensionMap.containsKey(mimeType);
        }

        return false;
    }

    /**
     * Return the MIME type for the given extension.
     * @param extension A file extension without the leading '.'
     * @return The MIME type for the given extension or null iff there is none.
     */
    public String getMimeTypeFromExtension(String extension) {
        if (extension != null && extension.length() > 0) {
            return mExtensionToMimeTypeMap.get(extension);
        }

        return null;
    }

    /**
     * Return true if the given extension has a registered MIME type.
     * @param extension A file extension without the leading '.'
     * @return True iff there is an extension entry in the map.
     */
    public boolean hasExtension(String extension) {
        if (extension != null && extension.length() > 0) {
            return mExtensionToMimeTypeMap.containsKey(extension);
        }
        return false;
    }

    /**
     * Return the registered extension for the given MIME type. Note that some
     * MIME types map to multiple extensions. This call will return the most
     * common extension for the given MIME type.
     * @param mimeType A MIME type (i.e. text/plain)
     * @return The extension for the given MIME type or null iff there is none.
     */
    public String getExtensionFromMimeType(String mimeType) {
        if (mimeType != null && mimeType.length() > 0) {
            return mMimeTypeToExtensionMap.get(mimeType);
        }

        return null;
    }

    /**
     * Get the singleton instance of MimeTypeMap.
     * @return The singleton instance of the MIME-type map.
     */
    public static MimeTypeUtil getSingleton() {
        if (mimeTypeUtil == null) {
        	mimeTypeUtil = new MimeTypeUtil();

            // The following table is based on /etc/mime.types data minus
            // chemical/* MIME types and MIME types that don't map to any
            // file extensions. We also exclude top-level domain names to
            // deal with cases like:
            //
            // mail.google.com/a/google.com
            //
            // and "active" MIME types (due to potential security issues).

            mimeTypeUtil.loadEntry("application/andrew-inset", "ez");
            mimeTypeUtil.loadEntry("application/dsptype", "tsp");
            mimeTypeUtil.loadEntry("application/futuresplash", "spl");
            mimeTypeUtil.loadEntry("application/hta", "hta");
            mimeTypeUtil.loadEntry("application/mac-binhex40", "hqx");
            mimeTypeUtil.loadEntry("application/mac-compactpro", "cpt");
            mimeTypeUtil.loadEntry("application/mathematica", "nb");
            mimeTypeUtil.loadEntry("application/msaccess", "mdb");
            mimeTypeUtil.loadEntry("application/oda", "oda");
            mimeTypeUtil.loadEntry("application/ogg", "ogg");
            mimeTypeUtil.loadEntry("application/pdf", "pdf");
            mimeTypeUtil.loadEntry("application/pgp-keys", "key");
            mimeTypeUtil.loadEntry("application/pgp-signature", "pgp");
            mimeTypeUtil.loadEntry("application/pics-rules", "prf");
            mimeTypeUtil.loadEntry("application/rar", "rar");
            mimeTypeUtil.loadEntry("application/rdf+xml", "rdf");
            mimeTypeUtil.loadEntry("application/rss+xml", "rss");
            mimeTypeUtil.loadEntry("application/zip", "zip");
            mimeTypeUtil.loadEntry("application/vnd.android.package-archive", "apk");
            mimeTypeUtil.loadEntry("application/vnd.cinderella", "cdy");
            mimeTypeUtil.loadEntry("application/vnd.ms-pki.stl", "stl");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.database", "odb");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.formula", "odf");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.graphics", "odg");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.graphics-template", "otg");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.image", "odi");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.spreadsheet", "ods");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.spreadsheet-template", "ots");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.text", "odt");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.text-master", "odm");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.text-template", "ott");
            mimeTypeUtil.loadEntry("application/vnd.oasis.opendocument.text-web", "oth");
            mimeTypeUtil.loadEntry("application/msword", "doc");
            mimeTypeUtil.loadEntry("application/msword", "dot");
            mimeTypeUtil.loadEntry("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
            mimeTypeUtil.loadEntry("application/vnd.openxmlformats-officedocument.wordprocessingml.template", "dotx");
            mimeTypeUtil.loadEntry("application/vnd.ms-excel", "xls");
            mimeTypeUtil.loadEntry("application/vnd.ms-excel", "xlt");
            mimeTypeUtil.loadEntry("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
            mimeTypeUtil.loadEntry("application/vnd.openxmlformats-officedocument.spreadsheetml.template", "xltx");
            mimeTypeUtil.loadEntry("application/vnd.ms-powerpoint", "ppt");
            mimeTypeUtil.loadEntry("application/vnd.ms-powerpoint", "pot");
            mimeTypeUtil.loadEntry("application/vnd.ms-powerpoint", "pps");
            mimeTypeUtil.loadEntry("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");
            mimeTypeUtil.loadEntry("application/vnd.openxmlformats-officedocument.presentationml.template", "potx");
            mimeTypeUtil.loadEntry("application/vnd.openxmlformats-officedocument.presentationml.slideshow", "ppsx");
            mimeTypeUtil.loadEntry("application/vnd.rim.cod", "cod");
            mimeTypeUtil.loadEntry("application/vnd.smaf", "mmf");
            mimeTypeUtil.loadEntry("application/vnd.stardivision.calc", "sdc");
            mimeTypeUtil.loadEntry("application/vnd.stardivision.draw", "sda");
            mimeTypeUtil.loadEntry("application/vnd.stardivision.impress", "sdd");
            mimeTypeUtil.loadEntry("application/vnd.stardivision.impress", "sdp");
            mimeTypeUtil.loadEntry("application/vnd.stardivision.math", "smf");
            mimeTypeUtil.loadEntry("application/vnd.stardivision.writer", "sdw");
            mimeTypeUtil.loadEntry("application/vnd.stardivision.writer", "vor");
            mimeTypeUtil.loadEntry("application/vnd.stardivision.writer-global", "sgl");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.calc", "sxc");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.calc.template", "stc");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.draw", "sxd");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.draw.template", "std");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.impress", "sxi");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.impress.template", "sti");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.math", "sxm");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.writer", "sxw");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.writer.global", "sxg");
            mimeTypeUtil.loadEntry("application/vnd.sun.xml.writer.template", "stw");
            mimeTypeUtil.loadEntry("application/vnd.visio", "vsd");
            mimeTypeUtil.loadEntry("application/x-abiword", "abw");
            mimeTypeUtil.loadEntry("application/x-apple-diskimage", "dmg");
            mimeTypeUtil.loadEntry("application/x-bcpio", "bcpio");
            mimeTypeUtil.loadEntry("application/x-bittorrent", "torrent");
            mimeTypeUtil.loadEntry("application/x-cdf", "cdf");
            mimeTypeUtil.loadEntry("application/x-cdlink", "vcd");
            mimeTypeUtil.loadEntry("application/x-chess-pgn", "pgn");
            mimeTypeUtil.loadEntry("application/x-cpio", "cpio");
            mimeTypeUtil.loadEntry("application/x-debian-package", "deb");
            mimeTypeUtil.loadEntry("application/x-debian-package", "udeb");
            mimeTypeUtil.loadEntry("application/x-director", "dcr");
            mimeTypeUtil.loadEntry("application/x-director", "dir");
            mimeTypeUtil.loadEntry("application/x-director", "dxr");
            mimeTypeUtil.loadEntry("application/x-dms", "dms");
            mimeTypeUtil.loadEntry("application/x-doom", "wad");
            mimeTypeUtil.loadEntry("application/x-dvi", "dvi");
            mimeTypeUtil.loadEntry("application/x-flac", "flac");
            mimeTypeUtil.loadEntry("application/x-font", "pfa");
            mimeTypeUtil.loadEntry("application/x-font", "pfb");
            mimeTypeUtil.loadEntry("application/x-font", "gsf");
            mimeTypeUtil.loadEntry("application/x-font", "pcf");
            mimeTypeUtil.loadEntry("application/x-font", "pcf.Z");
            mimeTypeUtil.loadEntry("application/x-freemind", "mm");
            mimeTypeUtil.loadEntry("application/x-futuresplash", "spl");
            mimeTypeUtil.loadEntry("application/x-gnumeric", "gnumeric");
            mimeTypeUtil.loadEntry("application/x-go-sgf", "sgf");
            mimeTypeUtil.loadEntry("application/x-graphing-calculator", "gcf");
            mimeTypeUtil.loadEntry("application/x-gtar", "gtar");
            mimeTypeUtil.loadEntry("application/x-gtar", "tgz");
            mimeTypeUtil.loadEntry("application/x-gtar", "taz");
            mimeTypeUtil.loadEntry("application/x-hdf", "hdf");
            mimeTypeUtil.loadEntry("application/x-ica", "ica");
            mimeTypeUtil.loadEntry("application/x-internet-signup", "ins");
            mimeTypeUtil.loadEntry("application/x-internet-signup", "isp");
            mimeTypeUtil.loadEntry("application/x-iphone", "iii");
            mimeTypeUtil.loadEntry("application/x-iso9660-image", "iso");
            mimeTypeUtil.loadEntry("application/x-jmol", "jmz");
            mimeTypeUtil.loadEntry("application/x-kchart", "chrt");
            mimeTypeUtil.loadEntry("application/x-killustrator", "kil");
            mimeTypeUtil.loadEntry("application/x-koan", "skp");
            mimeTypeUtil.loadEntry("application/x-koan", "skd");
            mimeTypeUtil.loadEntry("application/x-koan", "skt");
            mimeTypeUtil.loadEntry("application/x-koan", "skm");
            mimeTypeUtil.loadEntry("application/x-kpresenter", "kpr");
            mimeTypeUtil.loadEntry("application/x-kpresenter", "kpt");
            mimeTypeUtil.loadEntry("application/x-kspread", "ksp");
            mimeTypeUtil.loadEntry("application/x-kword", "kwd");
            mimeTypeUtil.loadEntry("application/x-kword", "kwt");
            mimeTypeUtil.loadEntry("application/x-latex", "latex");
            mimeTypeUtil.loadEntry("application/x-lha", "lha");
            mimeTypeUtil.loadEntry("application/x-lzh", "lzh");
            mimeTypeUtil.loadEntry("application/x-lzx", "lzx");
            mimeTypeUtil.loadEntry("application/x-maker", "frm");
            mimeTypeUtil.loadEntry("application/x-maker", "maker");
            mimeTypeUtil.loadEntry("application/x-maker", "frame");
            mimeTypeUtil.loadEntry("application/x-maker", "fb");
            mimeTypeUtil.loadEntry("application/x-maker", "book");
            mimeTypeUtil.loadEntry("application/x-maker", "fbdoc");
            mimeTypeUtil.loadEntry("application/x-mif", "mif");
            mimeTypeUtil.loadEntry("application/x-ms-wmd", "wmd");
            mimeTypeUtil.loadEntry("application/x-ms-wmz", "wmz");
            mimeTypeUtil.loadEntry("application/x-msi", "msi");
            mimeTypeUtil.loadEntry("application/x-ns-proxy-autoconfig", "pac");
            mimeTypeUtil.loadEntry("application/x-nwc", "nwc");
            mimeTypeUtil.loadEntry("application/x-object", "o");
            mimeTypeUtil.loadEntry("application/x-oz-application", "oza");
            mimeTypeUtil.loadEntry("application/x-pkcs12", "p12");
            mimeTypeUtil.loadEntry("application/x-pkcs7-certreqresp", "p7r");
            mimeTypeUtil.loadEntry("application/x-pkcs7-crl", "crl");
            mimeTypeUtil.loadEntry("application/x-quicktimeplayer", "qtl");
            mimeTypeUtil.loadEntry("application/x-shar", "shar");
            mimeTypeUtil.loadEntry("application/x-stuffit", "sit");
            mimeTypeUtil.loadEntry("application/x-sv4cpio", "sv4cpio");
            mimeTypeUtil.loadEntry("application/x-sv4crc", "sv4crc");
            mimeTypeUtil.loadEntry("application/x-tar", "tar");
            mimeTypeUtil.loadEntry("application/x-texinfo", "texinfo");
            mimeTypeUtil.loadEntry("application/x-texinfo", "texi");
            mimeTypeUtil.loadEntry("application/x-troff", "t");
            mimeTypeUtil.loadEntry("application/x-troff", "roff");
            mimeTypeUtil.loadEntry("application/x-troff-man", "man");
            mimeTypeUtil.loadEntry("application/x-ustar", "ustar");
            mimeTypeUtil.loadEntry("application/x-wais-source", "src");
            mimeTypeUtil.loadEntry("application/x-wingz", "wz");
            mimeTypeUtil.loadEntry("application/x-webarchive", "webarchive");
            mimeTypeUtil.loadEntry("application/x-x509-ca-cert", "crt");
            mimeTypeUtil.loadEntry("application/x-x509-user-cert", "crt");
            mimeTypeUtil.loadEntry("application/x-xcf", "xcf");
            mimeTypeUtil.loadEntry("application/x-xfig", "fig");
            mimeTypeUtil.loadEntry("application/xhtml+xml", "xhtml");
            mimeTypeUtil.loadEntry("audio/basic", "snd");
            mimeTypeUtil.loadEntry("audio/midi", "mid");
            mimeTypeUtil.loadEntry("audio/midi", "midi");
            mimeTypeUtil.loadEntry("audio/midi", "kar");
            mimeTypeUtil.loadEntry("audio/mpeg", "mpga");
            mimeTypeUtil.loadEntry("audio/mpeg", "mpega");
            mimeTypeUtil.loadEntry("audio/mpeg", "mp2");
            mimeTypeUtil.loadEntry("audio/mpeg", "mp3");
            mimeTypeUtil.loadEntry("audio/mpeg", "m4a");
            mimeTypeUtil.loadEntry("audio/mpegurl", "m3u");
            mimeTypeUtil.loadEntry("audio/prs.sid", "sid");
            mimeTypeUtil.loadEntry("audio/x-aiff", "aif");
            mimeTypeUtil.loadEntry("audio/x-aiff", "aiff");
            mimeTypeUtil.loadEntry("audio/x-aiff", "aifc");
            mimeTypeUtil.loadEntry("audio/x-gsm", "gsm");
            mimeTypeUtil.loadEntry("audio/x-mpegurl", "m3u");
            mimeTypeUtil.loadEntry("audio/x-ms-wma", "wma");
            mimeTypeUtil.loadEntry("audio/x-ms-wax", "wax");
            mimeTypeUtil.loadEntry("audio/x-pn-realaudio", "ra");
            mimeTypeUtil.loadEntry("audio/x-pn-realaudio", "rm");
            mimeTypeUtil.loadEntry("audio/x-pn-realaudio", "ram");
            mimeTypeUtil.loadEntry("audio/x-realaudio", "ra");
            mimeTypeUtil.loadEntry("audio/x-scpls", "pls");
            mimeTypeUtil.loadEntry("audio/x-sd2", "sd2");
            mimeTypeUtil.loadEntry("audio/x-wav", "wav");
            mimeTypeUtil.loadEntry("image/x-ms-bmp", "bmp");
            mimeTypeUtil.loadEntry("image/bmp", "bmp");
            mimeTypeUtil.loadEntry("image/gif", "gif");
            mimeTypeUtil.loadEntry("image/ico", "cur");
            mimeTypeUtil.loadEntry("image/ico", "ico");
            mimeTypeUtil.loadEntry("image/ief", "ief");
            mimeTypeUtil.loadEntry("image/jpeg", "jpeg");
            mimeTypeUtil.loadEntry("image/jpeg", "jpg");
            mimeTypeUtil.loadEntry("image/jpeg", "jpe");
            mimeTypeUtil.loadEntry("image/pcx", "pcx");
            mimeTypeUtil.loadEntry("image/png", "png");
            mimeTypeUtil.loadEntry("image/svg+xml", "svg");
            mimeTypeUtil.loadEntry("image/svg+xml", "svgz");
            mimeTypeUtil.loadEntry("image/tiff", "tiff");
            mimeTypeUtil.loadEntry("image/tiff", "tif");
            mimeTypeUtil.loadEntry("image/vnd.djvu", "djvu");
            mimeTypeUtil.loadEntry("image/vnd.djvu", "djv");
            mimeTypeUtil.loadEntry("image/vnd.wap.wbmp", "wbmp");
            mimeTypeUtil.loadEntry("image/x-cmu-raster", "ras");
            mimeTypeUtil.loadEntry("image/x-coreldraw", "cdr");
            mimeTypeUtil.loadEntry("image/x-coreldrawpattern", "pat");
            mimeTypeUtil.loadEntry("image/x-coreldrawtemplate", "cdt");
            mimeTypeUtil.loadEntry("image/x-corelphotopaint", "cpt");
            mimeTypeUtil.loadEntry("image/x-icon", "ico");
            mimeTypeUtil.loadEntry("image/x-jg", "art");
            mimeTypeUtil.loadEntry("image/x-jng", "jng");
            mimeTypeUtil.loadEntry("image/x-photoshop", "psd");
            mimeTypeUtil.loadEntry("image/x-portable-anymap", "pnm");
            mimeTypeUtil.loadEntry("image/x-portable-bitmap", "pbm");
            mimeTypeUtil.loadEntry("image/x-portable-graymap", "pgm");
            mimeTypeUtil.loadEntry("image/x-portable-pixmap", "ppm");
            mimeTypeUtil.loadEntry("image/x-rgb", "rgb");
            mimeTypeUtil.loadEntry("image/x-xbitmap", "xbm");
            mimeTypeUtil.loadEntry("image/x-xpixmap", "xpm");
            mimeTypeUtil.loadEntry("image/x-xwindowdump", "xwd");
            mimeTypeUtil.loadEntry("model/iges", "igs");
            mimeTypeUtil.loadEntry("model/iges", "iges");
            mimeTypeUtil.loadEntry("model/mesh", "msh");
            mimeTypeUtil.loadEntry("model/mesh", "mesh");
            mimeTypeUtil.loadEntry("model/mesh", "silo");
            mimeTypeUtil.loadEntry("text/calendar", "ics");
            mimeTypeUtil.loadEntry("text/calendar", "icz");
            mimeTypeUtil.loadEntry("text/comma-separated-values", "csv");
            mimeTypeUtil.loadEntry("text/css", "css");
            mimeTypeUtil.loadEntry("text/h323", "323");
            mimeTypeUtil.loadEntry("text/iuls", "uls");
            mimeTypeUtil.loadEntry("text/mathml", "mml");
            // add it first so it will be the default for ExtensionFromMimeType
            mimeTypeUtil.loadEntry("text/plain", "txt");
            mimeTypeUtil.loadEntry("text/plain", "asc");
            mimeTypeUtil.loadEntry("text/plain", "text");
            mimeTypeUtil.loadEntry("text/plain", "diff");
            mimeTypeUtil.loadEntry("text/plain", "po");     // reserve "pot" for vnd.ms-powerpoint
            mimeTypeUtil.loadEntry("text/richtext", "rtx");
            mimeTypeUtil.loadEntry("text/rtf", "rtf");
            mimeTypeUtil.loadEntry("text/texmacs", "ts");
            mimeTypeUtil.loadEntry("text/text", "phps");
            mimeTypeUtil.loadEntry("text/tab-separated-values", "tsv");
            mimeTypeUtil.loadEntry("text/xml", "xml");
            mimeTypeUtil.loadEntry("text/x-bibtex", "bib");
            mimeTypeUtil.loadEntry("text/x-boo", "boo");
            mimeTypeUtil.loadEntry("text/x-c++hdr", "h++");
            mimeTypeUtil.loadEntry("text/x-c++hdr", "hpp");
            mimeTypeUtil.loadEntry("text/x-c++hdr", "hxx");
            mimeTypeUtil.loadEntry("text/x-c++hdr", "hh");
            mimeTypeUtil.loadEntry("text/x-c++src", "c++");
            mimeTypeUtil.loadEntry("text/x-c++src", "cpp");
            mimeTypeUtil.loadEntry("text/x-c++src", "cxx");
            mimeTypeUtil.loadEntry("text/x-chdr", "h");
            mimeTypeUtil.loadEntry("text/x-component", "htc");
            mimeTypeUtil.loadEntry("text/x-csh", "csh");
            mimeTypeUtil.loadEntry("text/x-csrc", "c");
            mimeTypeUtil.loadEntry("text/x-dsrc", "d");
            mimeTypeUtil.loadEntry("text/x-haskell", "hs");
            mimeTypeUtil.loadEntry("text/x-java", "java");
            mimeTypeUtil.loadEntry("text/x-literate-haskell", "lhs");
            mimeTypeUtil.loadEntry("text/x-moc", "moc");
            mimeTypeUtil.loadEntry("text/x-pascal", "p");
            mimeTypeUtil.loadEntry("text/x-pascal", "pas");
            mimeTypeUtil.loadEntry("text/x-pcs-gcd", "gcd");
            mimeTypeUtil.loadEntry("text/x-setext", "etx");
            mimeTypeUtil.loadEntry("text/x-tcl", "tcl");
            mimeTypeUtil.loadEntry("text/x-tex", "tex");
            mimeTypeUtil.loadEntry("text/x-tex", "ltx");
            mimeTypeUtil.loadEntry("text/x-tex", "sty");
            mimeTypeUtil.loadEntry("text/x-tex", "cls");
            mimeTypeUtil.loadEntry("text/x-vcalendar", "vcs");
            mimeTypeUtil.loadEntry("text/x-vcard", "vcf");
            mimeTypeUtil.loadEntry("video/3gpp", "3gp");
            mimeTypeUtil.loadEntry("video/3gpp", "3g2");
            mimeTypeUtil.loadEntry("video/dl", "dl");
            mimeTypeUtil.loadEntry("video/dv", "dif");
            mimeTypeUtil.loadEntry("video/dv", "dv");
            mimeTypeUtil.loadEntry("video/fli", "fli");
            mimeTypeUtil.loadEntry("video/mpeg", "mpeg");
            mimeTypeUtil.loadEntry("video/mpeg", "mpg");
            mimeTypeUtil.loadEntry("video/mpeg", "mpe");
            mimeTypeUtil.loadEntry("video/mp4", "mp4");
            mimeTypeUtil.loadEntry("video/mpeg", "VOB");
            mimeTypeUtil.loadEntry("video/quicktime", "qt");
            mimeTypeUtil.loadEntry("video/quicktime", "mov");
            mimeTypeUtil.loadEntry("video/vnd.mpegurl", "mxu");
            mimeTypeUtil.loadEntry("video/x-la-asf", "lsf");
            mimeTypeUtil.loadEntry("video/x-la-asf", "lsx");
            mimeTypeUtil.loadEntry("video/x-mng", "mng");
            mimeTypeUtil.loadEntry("video/x-ms-asf", "asf");
            mimeTypeUtil.loadEntry("video/x-ms-asf", "asx");
            mimeTypeUtil.loadEntry("video/x-ms-wm", "wm");
            mimeTypeUtil.loadEntry("video/x-ms-wmv", "wmv");
            mimeTypeUtil.loadEntry("video/x-ms-wmx", "wmx");
            mimeTypeUtil.loadEntry("video/x-ms-wvx", "wvx");
            mimeTypeUtil.loadEntry("video/x-msvideo", "avi");
            mimeTypeUtil.loadEntry("video/x-sgi-movie", "movie");
            mimeTypeUtil.loadEntry("x-conference/x-cooltalk", "ice");
            mimeTypeUtil.loadEntry("x-epoc/x-sisx-app", "sisx");
        }

        return mimeTypeUtil;
    }
}
