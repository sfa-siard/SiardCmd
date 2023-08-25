USE [master]


SET ANSI_NULLS ON

SET ANSI_PADDING ON

CREATE TYPE [dbo].[dtDay2] FROM [tinyint] NOT NULL;

CREATE TABLE [dbo].[CollectionEvent]
(
    [ID] [int] IDENTITY (1,1) NOT NULL,
    [Day]     [dbo].[dtDay2]       NULL
);

SET ANSI_PADDING OFF
