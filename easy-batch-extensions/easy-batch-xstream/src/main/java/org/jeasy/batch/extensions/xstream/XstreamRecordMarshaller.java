/*
 * The MIT License
 *
 *   Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package org.jeasy.batch.extensions.xstream;

import com.thoughtworks.xstream.XStream;
import org.jeasy.batch.core.marshaller.RecordMarshaller;
import org.jeasy.batch.core.record.Record;
import org.jeasy.batch.xml.XmlRecord;

import static org.jeasy.batch.core.util.Utils.checkNotNull;

/**
 * Marshals an object to XML using <a href="http://xstream.codehaus.org/">XStream</a>.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 * @param <P> type of the record's payload
 */
public class XstreamRecordMarshaller<P> implements RecordMarshaller<P, String> {

    private XStream xStream;

    /**
     * Create a new {@link XstreamRecordMarshaller}.
     */
    public XstreamRecordMarshaller() {
        this.xStream = new XStream();
    }

    /**
     * Create a new {@link XstreamRecordMarshaller}.
     *
     * @param elementName the alias of the object in the corresponding xml tag
     * @param type        the type of the object to marshal
     */
    public XstreamRecordMarshaller(final String elementName, final Class<?> type) {
        this();
        checkNotNull(elementName, "element name");
        checkNotNull(type, "target type");
        xStream.alias(elementName, type);
    }

    /**
     * Create a new {@link XstreamRecordMarshaller}.
     *
     * @param xStream a pre-configured xstream instance
     */
    public XstreamRecordMarshaller(final XStream xStream) {
        checkNotNull(xStream, "xStream");
        this.xStream = xStream;
    }

    @Override
    public XmlRecord processRecord(final Record<P> record) {
        return new XmlRecord(record.getHeader(), xStream.toXML(record.getPayload()));
    }
}
