import React from "react";
import PropTypes from "prop-types";
import CollectionsList from "./CollectionsList";

import {
  nonPersonalOrArchivedCollection,
  currentUserPersonalCollections as getCurrentUserPersonalCollections,
} from "metabase/collections/utils";

import { Container } from "./Collections.styled";

const propTypes = {
  collectionId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  currentUserId: PropTypes.number,
  handleToggleMobileSidebar: PropTypes.func.isRequired,
  list: PropTypes.array,
  onClose: PropTypes.func.isRequired,
  onOpen: PropTypes.func.isRequired,
  openCollections: PropTypes.array,
};

export default function Collections({
  collectionId,
  currentUserId,
  handleToggleMobileSidebar,
  list,
  onClose,
  onOpen,
  openCollections,
}) {
  function filterPersonalCollections(collection) {
    return !collection.archived;
  }

  const currentUserPersonalCollections = getCurrentUserPersonalCollections(
    list,
    currentUserId,
  );

  return (
    <Container>
      <CollectionsList
        handleToggleMobileSidebar={handleToggleMobileSidebar}
        openCollections={openCollections}
        onClose={onClose}
        onOpen={onOpen}
        collections={list}
        filter={nonPersonalOrArchivedCollection}
        currentCollection={collectionId}
      />

      <div>
        <CollectionsList
          handleToggleMobileSidebar={handleToggleMobileSidebar}
          openCollections={openCollections}
          onClose={onClose}
          onOpen={onOpen}
          collections={currentUserPersonalCollections}
          filter={filterPersonalCollections}
          currentCollection={collectionId}
        />
      </div>
    </Container>
  );
}

Collections.propTypes = propTypes;
