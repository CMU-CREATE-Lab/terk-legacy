Created the key pair found in this directory so I could give the TeRKPeerCommon, MRPLPeer, and TeRKClientComponents
assemblies a strong name (required for using with MSRS).

Here's what I did:

      >sn -k terk_key_pair.snk

      Microsoft (R) .NET Framework Strong Name Utility  Version 2.0.50727.42
      Copyright (c) Microsoft Corporation.  All rights reserved.

      Key pair written to terk_key_pair.snk

      >sn -p terk_key_pair.snk terk_public_key.snk

      Microsoft (R) .NET Framework Strong Name Utility  Version 2.0.50727.42
      Copyright (c) Microsoft Corporation.  All rights reserved.

      Public key written to terk_public_key.snk

      >

Note that I had to put this directory on my path in order for the sn.exe program to be found:

      D:\Program Files\Microsoft Visual Studio 8\SDK\v2.0\Bin

I found these instructions at:

      http://msdn2.microsoft.com/en-us/library/6f05ezxy.aspx

I caused the C# compiler to give the assemblies a strong name by including the /KEYFILE option and had it point to the
terk_key_pair.snk file.