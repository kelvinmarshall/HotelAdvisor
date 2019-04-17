/*
 * Copyright (c) 2015 Algolia
 * http://www.algolia.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package dev.marshall.hoteladvisor.io_nearby;

import org.json.JSONObject;

import dev.marshall.hoteladvisor.model.HotelSearch;

/**
 * Parses `Movie` instances from their JSON representation.
 */
public class HotelJsonParser
{
    /**
     * Parse a single movie record.
     *
     * @param jsonObject JSON object.
     * @return Parsed movie, or null if error.
     */
    public HotelSearch parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;

        String name = jsonObject.optString("Name");
        String image = jsonObject.optString("Image");
        String rating = jsonObject.optString("Rating");
        String location = jsonObject.optString("Location");
        String price = jsonObject.optString("Price");
        String objectID=jsonObject.optString("objectID");
        if (name != null && image != null && rating != null && location != null && price != null) {
            return new HotelSearch(name, image, rating, location,price,objectID);

        }
        return null;
    }
}
