package com.SIMATS.myapplication.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Singleton class for Volley RequestQueue management.
 * Provides centralized network request handling for the Education Stream
 * Advisor App.
 * 
 * Usage:
 * VolleySingleton.getInstance(context).addToRequestQueue(request);
 */
public class VolleySingleton {

    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    /**
     * Private constructor - use getInstance() instead.
     * 
     * @param context Application context
     */
    private VolleySingleton(Context context) {
        ctx = context.getApplicationContext();
        requestQueue = getRequestQueue();

        // Initialize ImageLoader with LruCache for image caching
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    /**
     * Get singleton instance of VolleySingleton.
     * 
     * @param context Any context (will be converted to application context)
     * @return VolleySingleton instance
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    /**
     * Get the RequestQueue, creating it if necessary.
     * 
     * @return Volley RequestQueue
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // Use application context to prevent memory leaks
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }

    /**
     * Add a request to the RequestQueue with default retry policy.
     * 
     * @param req The request to add
     * @param <T> Type of the request response
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // Apply default retry policy from ApiConfig
        req.setRetryPolicy(new DefaultRetryPolicy(
                ApiConfig.REQUEST_TIMEOUT_MS,
                ApiConfig.MAX_RETRIES,
                ApiConfig.BACKOFF_MULTIPLIER));
        getRequestQueue().add(req);
    }

    /**
     * Add a request with a custom tag for cancellation.
     * 
     * @param req The request to add
     * @param tag Tag for request identification
     * @param <T> Type of the request response
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);
        req.setRetryPolicy(new DefaultRetryPolicy(
                ApiConfig.REQUEST_TIMEOUT_MS,
                ApiConfig.MAX_RETRIES,
                ApiConfig.BACKOFF_MULTIPLIER));
        getRequestQueue().add(req);
    }

    /**
     * Add a request with custom timeout settings.
     * 
     * @param req        The request to add
     * @param timeoutMs  Custom timeout in milliseconds
     * @param maxRetries Maximum number of retries
     * @param <T>        Type of the request response
     */
    public <T> void addToRequestQueue(Request<T> req, int timeoutMs, int maxRetries) {
        req.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs,
                maxRetries,
                ApiConfig.BACKOFF_MULTIPLIER));
        getRequestQueue().add(req);
    }

    /**
     * Cancel all pending requests with the given tag.
     * Useful for cancelling requests when an Activity is destroyed.
     * 
     * @param tag The tag of requests to cancel
     */
    public void cancelRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    /**
     * Cancel all pending requests.
     * Use with caution - typically called when app is closing.
     */
    public void cancelAllRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll(request -> true);
        }
    }

    /**
     * Get the ImageLoader for loading images from URLs.
     * 
     * @return Volley ImageLoader
     */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * Clear the image cache.
     * Useful when memory is low or images need to be refreshed.
     */
    public void clearImageCache() {
        if (imageLoader != null) {
            imageLoader.setBatchedResponseDelay(0);
        }
    }
}
