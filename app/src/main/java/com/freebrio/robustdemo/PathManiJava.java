package com.freebrio.robustdemo;

import android.content.Context;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchManipulate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * introduce：here is introduce
 * author：sunwentao
 * email：wentao.sun@freebrio.com
 * data: 2021/04/25
 */
class PathManiJava extends PatchManipulate {
    @Override
    protected List<Patch> fetchPatchList(Context context) {
        Patch patch = new Patch();
        patch.setName("test");
        patch.setLocalPath(context.getExternalCacheDir().getAbsolutePath() + File.separator + "robust" + File.separator + "patch");
        patch.setPatchesInfoImplClassFullName("com.freebrio.robustdemo.PathManiJava");
        List list = new ArrayList();
        list.add(patch);
        return list;
    }

    @Override
    protected boolean verifyPatch(Context context, Patch patch) {
        return true;
    }

    @Override
    protected boolean ensurePatchExist(Patch patch) {
        return true;
    }
}
