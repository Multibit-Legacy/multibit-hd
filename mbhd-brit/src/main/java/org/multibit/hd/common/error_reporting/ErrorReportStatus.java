package org.multibit.hd.common.error_reporting;

/**
 * <p>Enum to provide the following to error reporting code:</p>
 * <ul>
 * <li>Standard outcomes</li>
 * </ul>
 *
 * @since 0.1.0
 *  
 */
public enum ErrorReportStatus {

    /**
     * Upload was OK and the server has provided a Location header
     * containing a possible fix
     */
    UPLOAD_OK_KNOWN,
    /**
     * Upload was OK and the server has accepted the new entry
     * The error is currently unknown
     */
    UPLOAD_OK_UNKNOWN,
    /**
     * Upload failed for the reason entered in the log
     * The user won't be able to do anything to correct the problem
     */
    UPLOAD_FAILED,

}