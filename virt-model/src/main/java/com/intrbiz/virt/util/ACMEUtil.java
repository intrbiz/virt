package com.intrbiz.virt.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;

import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.toolbox.AcmeUtils;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;

public class ACMEUtil
{
    public static String keyPairToString(KeyPair keyPair)
    {
        StringWriter sw = new StringWriter();
        try
        {
            try
            {
                KeyPairUtils.writeKeyPair(keyPair, sw);
            }
            finally
            {
                sw.close();
            }
        }
        catch (IOException e)
        {
        }
        return sw.toString();
    }
    
    public static KeyPair keyPairFromString(String keyPair)
    {
        try (StringReader sr = new StringReader(keyPair))
        {
            return KeyPairUtils.readKeyPair(sr);
        }
        catch (IOException e)
        {
        }
        return null;
    }
    
    public static String csrToString(CSRBuilder csr)
    {
        StringWriter sw = new StringWriter();
        try
        {
            try
            {
                csr.write(sw);
            }
            finally
            {
                sw.close();
            }
        }
        catch (IOException e)
        {
        }
        return sw.toString();
    }
    
    public static String certToBundleString(Certificate cert)
    {
        StringWriter sw = new StringWriter();
        try
        {
            try
            {
                cert.writeCertificate(sw);
            }
            finally
            {
                sw.close();
            }
        }
        catch (IOException e)
        {
        }
        return sw.toString();
    }
    
    public static String certToString(Certificate cert)
    {
        StringWriter sw = new StringWriter();
        try
        {
            try
            {
                AcmeUtils.writeToPem(cert.getCertificate().getEncoded(), AcmeUtils.PemLabel.CERTIFICATE, sw);
            }
            catch (CertificateEncodingException e)
            {
            }
            finally
            {
                sw.close();
            }
        }
        catch (IOException e)
        {
        }
        return sw.toString();
    }
}
