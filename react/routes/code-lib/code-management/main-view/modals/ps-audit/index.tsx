import React, { useState } from "react";
import { Loading } from "@choerodon/components";
import Apis from "../../../apis";
import get from "lodash/get";
import { useEffect } from "react";

const AuditModal = ({ modal, organizationId, projectId, onOk }: any) => {
  const [isAuditing, setAuditing] = useState(true);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAuditingStatus();
  }, []);

  useEffect(() => {
    if (isAuditing) {
      modal.update({
        okCancel: false,
        okText: "我知道了",
      });
    } else {
      modal.update({
        onOk: () => {
          onOk();
          return true;
        },
        okCancel: true,
        okText: "确定",
      });
    }
  }, [isAuditing]);

  async function getAuditingStatus() {
    try {
      const res = await Apis.batchAuditStatus(organizationId, projectId);
      if (res && res.failed) {
        return res;
      }
      const isFixed =
        ["COMPLETED", "FAILED"].includes(get(res, "status")) ||
        !get(res, "status");
      setAuditing(!isFixed);
      setLoading(false);
      return true;
    } catch (error) {
      setLoading(false);
      throw new Error("check auditing data error, please try again");
    }
  }

  if (loading) {
    return <Loading display style={{
      minHeight: '100px'
    }} />;
  }

  return (
    <div>
      {isAuditing
        ? "手动审计正在进行中...."
        : "确定进行手动审计吗？这个过程可能需要一段时间，可以先进行其他操作"}
    </div>
  );
};

const FixModal = ({ modal, organizationId, projectId, onOk }: any) => {
  const [isFixing, setFixing] = useState(true);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAuditingStatus();
  }, []);

  useEffect(() => {
    if (isFixing) {
      modal.update({
        okCancel: false,
        okText: "我知道了",
      });
    } else {
      modal.update({
        onOk: () => {
          onOk();
          return true;
        },
        okCancel: true,
        okText: "确定",
      });
    }
  }, [isFixing]);

  async function getAuditingStatus() {
    setLoading(true);
    try {
      const res = await Apis.batchAuditStatus(organizationId, projectId);
      if (res && res.failed) {
        return res;
      }
      const isFixed =
        ["COMPLETED", "FAILED"].includes(get(res, "status")) ||
        !get(res, "status");
      setFixing(!isFixed);
      setLoading(false);
      return true;
    } catch (error) {
      setLoading(false);
      throw new Error("check auditing data error, please try again");
    }
  }

  if (loading) {
    return <Loading display style={{
      minHeight: '100px'
    }} />;
  }

  return (
    <div>
      {isFixing
        ? "批量修复正在进行中...."
        : "确定进行批量修复吗？这个过程可能需要一段时间，可以先进行其他操作"}
    </div>
  );
};

export { AuditModal, FixModal };
