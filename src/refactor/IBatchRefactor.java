package refactor;

import com.intellij.psi.*;
import refactor.config.IRenameInfo;
import refactor.mybatis.dom.model.Mapper;
import refactor.mybatis.dom.model.Result;
import refactor.mybatis.dom.model.ResultMap;
import refactor.mybatis.utils.MapperUtils;

import java.util.Collection;
import java.util.List;

public interface IBatchRefactor {
    static String GET = "get";
    static String SET = "set";

    static String makeMethodName(String field) {
        char[] chars = field.toCharArray();
        char c = Character.toUpperCase(chars[0]);
        chars[0] = c;

        return String.valueOf(chars);
    }

    default void startRefactoring() {
        List<IRenameInfo> infos = this._getRenameInfos();
        Logger.log(Status.INFO, "Rename infos:" + infos);
        for (IRenameInfo info : infos) {
            this.rename(info);
            Logger.log(Status.INFO, info + " renamed!");

        }
    }

    default void rename(IRenameInfo info) {
        String namespace = info.getNamespace();
        String oldID = info.getOldId();
        String newID = info.getNewId();
        IRefactor refactor = this._getRefactor();
        PsiClass compunit = refactor.findUnit(namespace);
//		System.out.println("compunit:" +compunit);

        if (info.isTable()) {
            refactor.renameCompUnit(compunit, newID);
        } else {
            PsiField field = refactor.readField(compunit, oldID);
            PsiType psiType = field.getType();
            refactor.makeChangetoField(field, newID);

//			System.out.println("===========" +qname);
            renameMethod(SET, oldID, newID, refactor, compunit, psiType);
            renameMethod(GET, oldID, newID, refactor, compunit, null);
            renameMapper(compunit, oldID, newID);
        }
    }

    default void renameMapper(PsiClass compunit, String oldName, String newName) {
        Collection<Mapper> mappers = MapperUtils.findMappers(this._getRefactor()._getProject(), compunit);
        for (Mapper mapper : mappers) {
//			VirtualFile vf = mapper.getXmlTag().getOriginalElement().getContainingFile().getVirtualFile();
//			Logger.log("******** inserts:" + mapper.getInserts().size());
//			Logger.log("******** resultMap:" + mapper.getResultMaps().get(0).getResults().get(3).getProperty());
            this.renameXmlResultSet(mapper, oldName, newName);
//            Logger.log("*****Text : " + LoadTextUtil.loadText(vf));
        }
    }

    default void renameXmlResultSet(Mapper mapper, String oldName, String newName) {
        List<ResultMap> resultMaps = mapper.getResultMaps();
        for (ResultMap resultMap : resultMaps) {
            List<Result> results = resultMap.getResults();
            for (Result result : results) {
                PsiElement elemnent = result.getProperty().getValue();
                if (result.getProperty().getStringValue().equals(oldName)) {
                    this._getRefactor().renameUnit(elemnent, newName);
                }
            }
        }
    }

    default void renameMethod(String prefix, String oldID, String newID, IRefactor refactor,
                              PsiClass compunit, PsiType psiType) {
        PsiMethod method = refactor.readMethod(compunit, prefix + makeMethodName(oldID),
                psiType == null ? new PsiType[]{} : new PsiType[]{psiType});
//		System.out.println(qname+"=============" +method);
        if (method != null)
            refactor.makeChangetoMethod(method, prefix + makeMethodName(newID));
    }

    IRefactor _getRefactor();

    List<IRenameInfo> _getRenameInfos();
}
