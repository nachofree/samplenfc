//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.sample_with_sdk;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.nfc.tech.NfcA;
import android.os.SystemClock;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class WaveshareNFCWriter {
    byte[] a = new byte['\ue2e0'];
    byte[] b = new byte['\ue2e0'];
    public int c = 0;
    int[] d = new int[]{0, 250, 296, 400, 800, 880, 264, 296};
    int[] e = new int[]{0, 122, 128, 300, 480, 528, 176, 128};
    byte[] f = new byte[]{0, 19, 19, 103, 123, 123, 124, 77};
    int[] g = new int[]{0, 250, 296, 150, 400, 484, 48, 64};
    byte[] h = new byte[]{0, 4, 7, 10, 14, 17, 16, 8};
    int[] i = new int[]{0, 0, 0, 0, 0, 0, 0, 1};

    WaveshareNFCWriter() {
        this.c = 0;
    }

    void a() {
        this.c = 0;
    }

    public int b() {
        return this.c;
    }

    int a(NfcA var1, int var2, Bitmap var3) {
        try {
            Boolean var4 = false;
            var1.connect();
            var1.setTimeout(1000);
            this.c = 0;
            byte[] var7 = "WSDZ10m".getBytes();
            byte[] var5 = var1.transceive(new byte[]{48, 0});
            byte[] var6 = Arrays.copyOf(var5, 7);
            if (!Arrays.equals(var6, var7)) {
                return 0;
            }

            this.a(var1);
            var4 = this.b(var1, var2, var3);
            if (var4) {
                return 1;
            }
        } catch (IOException var8) {
            var8.printStackTrace();
            this.c = -1;
        }

        this.c = -1;
        return 0;
    }

    private void a(NfcA var1) {
        byte[] var2 = new byte[48];

        try {
            byte[] var3 = var1.transceive(new byte[]{48, 4});
            System.arraycopy(var3, 0, var2, 0, 16);
            byte[] var4 = var1.transceive(new byte[]{48, 8});
            System.arraycopy(var4, 0, var2, 16, 16);
            byte[] var5 = var1.transceive(new byte[]{48, 12});
            System.arraycopy(var5, 0, var2, 32, 16);
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        byte[] var8 = new byte[]{3, 39, -44, 15, 21, 97, 110, 100, 114, 111, 105, 100, 46, 99, 111, 109, 58, 112, 107, 103, 119, 97, 118, 101, 115, 104, 97, 114, 101, 46, 102, 101, 110, 103, 46, 110, 102, 99, 116, 97, 103, -2, 0, 0, 0, 0, 0, 0};
        if (!Arrays.equals(var8, var2)) {
            for(int var9 = 0; var9 < 11; ++var9) {
                try {
                    var1.transceive(new byte[]{-94, (byte)(var9 + 4), var8[var9 * 4], var8[var9 * 4 + 1], var8[var9 * 4 + 2], var8[var9 * 4 + 3]});
                } catch (IOException var6) {
                    var6.printStackTrace();
                }
            }
        }

    }

    public boolean b(NfcA var1, int var2, Bitmap var3) {
        try {
            byte[] var4 = var1.transceive(new byte[]{-51, 13});
            if (var4[0] == 0 && var4[1] == 0) {
                var4 = var1.transceive(new byte[]{-51, 0, this.h[var2]});
                if (var4[0] == 0 && var4[1] == 0) {
                    SystemClock.sleep(50L);
                    var4 = var1.transceive(new byte[]{-51, 1});
                    if (var4[0] == 0 && var4[1] == 0) {
                        SystemClock.sleep(20L);
                        var4 = var1.transceive(new byte[]{-51, 2});
                        if (var4[0] == 0 && var4[1] == 0) {
                            SystemClock.sleep(20L);
                            var4 = var1.transceive(new byte[]{-51, 3});
                            if (var4[0] == 0 && var4[1] == 0) {
                                SystemClock.sleep(20L);
                                var4 = var1.transceive(new byte[]{-51, 5});
                                if (var4[0] == 0 && var4[1] == 0) {
                                    SystemClock.sleep(20L);
                                    var4 = var1.transceive(new byte[]{-51, 6});
                                    if (var4[0] == 0 && var4[1] == 0) {
                                        SystemClock.sleep(100L);
                                        Bitmap var5 = this.a(var3);
                                        int[] var6 = new int[var3.getWidth() * var3.getHeight()];
                                        int[] var7 = new int[var5.getWidth() * var5.getHeight()];
                                        if (var2 != 1 && var2 != 2 && var2 != 6 && var2 != 7) {
                                            var3.getPixels(var6, 0, var3.getWidth(), 0, 0, var3.getWidth(), var3.getHeight());
                                            var3.getPixels(var7, 0, var3.getWidth(), 0, 0, var3.getWidth(), var3.getHeight());
                                        } else {
                                            if (var3 == null) {
                                                return false;
                                            }

                                            int var8 = var3.getWidth();
                                            int var9 = var3.getHeight();
                                            Matrix var10 = new Matrix();
                                            var10.setRotate(270.0F);
                                            Bitmap var11 = Bitmap.createBitmap(var3, 0, 0, var8, var9, var10, false);
                                            Bitmap var12 = Bitmap.createBitmap(var5, 0, 0, var8, var9, var10, false);
                                            var11.getPixels(var6, 0, var11.getWidth(), 0, 0, var11.getWidth(), var11.getHeight());
                                            var12.getPixels(var7, 0, var11.getWidth(), 0, 0, var11.getWidth(), var11.getHeight());
                                        }

                                        Log.e("EPD_high = ", " " + this.e[var2]);
                                        Log.e("EPD_width = ", " " + this.d[var2]);
                                        if (this.i[var2] == 0) {
                                            if (var2 == 1) {
                                                for(int var32 = 0; var32 < 250; ++var32) {
                                                    for(int var40 = 0; var40 < 16; ++var40) {
                                                        byte var28 = 0;

                                                        for(int var47 = 0; var47 < 8; ++var47) {
                                                            var28 = (byte)(var28 << 1);
                                                            if ((var6[var47 + var40 * 8 + var32 * 128] & 255) > 128) {
                                                                var28 = (byte)(var28 | 1);
                                                            }
                                                        }

                                                        this.a[var32 * 16 + var40] = var28;
                                                        this.b[var32 * 16 + var40] = 0;
                                                    }
                                                }
                                            } else {
                                                for(int var33 = 0; var33 < this.e[var2]; ++var33) {
                                                    for(int var41 = 0; var41 < this.d[var2] / 8; ++var41) {
                                                        byte var29 = 0;

                                                        for(int var48 = 0; var48 < 8; ++var48) {
                                                            var29 = (byte)(var29 << 1);
                                                            if ((var6[var48 + var41 * 8 + var33 * this.d[var2]] & 255) > 128) {
                                                                var29 = (byte)(var29 | 1);
                                                            }
                                                        }

                                                        this.a[var33 * (this.d[var2] / 8) + var41] = var29;
                                                        this.b[var33 * (this.d[var2] / 8) + var41] = 0;
                                                    }
                                                }
                                            }
                                        } else if (this.i[var2] == 1) {
                                            for(int var34 = 0; var34 < this.e[var2]; ++var34) {
                                                for(int var42 = 0; var42 < this.d[var2] / 8; ++var42) {
                                                    byte var30 = 0;
                                                    byte var31 = 0;

                                                    for(int var49 = 0; var49 < 8; ++var49) {
                                                        var30 = (byte)(var30 << 1);
                                                        var31 = (byte)(var31 << 1);
                                                        if (var6[var49 + var42 * 8 + var34 * this.d[var2]] == -1) {
                                                            var30 = (byte)(var30 | 1);
                                                        }

                                                        if ((var7[var49 + var42 * 8 + var34 * this.d[var2]] & 255) > 128) {
                                                            var31 = (byte)(var31 | 1);
                                                        }
                                                    }

                                                    this.a[var34 * (this.d[var2] / 8) + var42] = var30;
                                                    this.b[var34 * (this.d[var2] / 8) + var42] = var31;
                                                }
                                            }
                                        }

                                        var4 = var1.transceive(new byte[]{-51, 7, 0});
                                        if (var4[0] == 0 && var4[1] == 0) {
                                            Log.e("Packet_number = ", " " + this.g[var2]);
                                            Log.e("Packet_size = ", " " + this.f[var2]);

                                            for(int var35 = 0; var35 < this.g[var2]; ++var35) {
                                                byte[] var43 = new byte[this.f[var2]];
                                                System.arraycopy(new byte[]{-51, 8, (byte)(this.f[var2] - 3)}, 0, var43, 0, 3);
                                                if (var2 == 6) {
                                                    for(int var50 = 0; var50 < 121; ++var50) {
                                                        var43[var50 + 3] = -1;
                                                    }
                                                } else {
                                                    System.arraycopy(this.a, var35 * (this.f[var2] - 3), var43, 3, this.f[var2] - 3);
                                                }

                                                var4 = var1.transceive(var43);
                                                if (var4[0] != 0 || var4[1] != 0) {
                                                    return false;
                                                }

                                                if (var2 != 6 && var2 != 7) {
                                                    this.c = var35 * 100 / this.g[var2];
                                                } else {
                                                    this.c = var35 * 50 / this.g[var2];
                                                }

                                                SystemClock.sleep(2L);
                                            }

                                            if (var2 == 5) {
                                                byte[] var36 = new byte[113];
                                                System.arraycopy(new byte[]{-51, 8, 120}, 0, var36, 0, 3);

                                                for(int var44 = 0; var44 < 110; ++var44) {
                                                    var36[var44 + 3] = -1;
                                                }

                                                var1.transceive(var36);
                                            }

                                            var4 = var1.transceive(new byte[]{-51, 24});
                                            if (var4[0] == 0 && var4[1] == 0) {
                                                if (var2 != 6) {
                                                    if (var2 == 7) {
                                                        for(int var38 = 0; var38 < this.g[var2]; ++var38) {
                                                            byte[] var46 = new byte[this.f[var2]];
                                                            System.arraycopy(new byte[]{-51, 8, (byte)(this.f[var2] - 3)}, 0, var46, 0, 3);
                                                            System.arraycopy(this.b, var38 * (this.f[var2] - 3), var46, 3, this.f[var2] - 3);
                                                            var4 = var1.transceive(var46);
                                                            if (var4[0] != 0 || var4[1] != 0) {
                                                                return false;
                                                            }

                                                            this.c = var38 * 50 / this.g[var2] + 50;
                                                            SystemClock.sleep(1L);
                                                        }
                                                    }
                                                } else {
                                                    SystemClock.sleep(100L);

                                                    for(int var37 = 0; var37 < 48; ++var37) {
                                                        byte[] var45 = new byte[124];
                                                        System.arraycopy(new byte[]{-51, 25, 121}, 0, var45, 0, 3);
                                                        System.arraycopy(this.a, var37 * 121, var45, 3, 121);
                                                        this.c = var37 * 50 / 48 + 51;
                                                        var4 = var1.transceive(var45);
                                                        if (var4[0] != 0 || var4[1] != 0) {
                                                            return false;
                                                        }

                                                        SystemClock.sleep(2L);
                                                    }

                                                    SystemClock.sleep(100L);
                                                }

                                                SystemClock.sleep(200L);
                                                var4 = var1.transceive(new byte[]{-51, 9});
                                                if (var4[0] == 0 && var4[1] == 0) {
                                                    SystemClock.sleep(200L);
                                                    int var39 = 0;

                                                    while(true) {
                                                        ++var39;
                                                        var4 = var1.transceive(new byte[]{-51, 10});
                                                        if (var4[0] == -1 && var4[1] == 0) {
                                                            var4 = var1.transceive(new byte[]{-51, 4});
                                                            if (var4[0] == 0 && var4[1] == 0) {
                                                                this.c = 100;
                                                                return true;
                                                            }

                                                            return false;
                                                        }

                                                        if (var39 > 100) {
                                                        }

                                                        SystemClock.sleep(25L);
                                                    }
                                                } else {
                                                    return false;
                                                }
                                            } else {
                                                return false;
                                            }
                                        } else {
                                            return false;
                                        }
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (IOException var13) {
            var13.printStackTrace();
            return false;
        }
    }

    Bitmap a(Bitmap var1) {
        new ArrayList();
        var1 = Bitmap.createScaledBitmap(var1, var1.getWidth(), var1.getHeight(), false);
        b var4 = new b(var1);
        Bitmap var2 = var4.a();
        return var2;
    }
}
